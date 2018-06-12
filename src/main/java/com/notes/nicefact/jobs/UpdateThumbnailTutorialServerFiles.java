package com.notes.nicefact.jobs;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.notes.nicefact.entity.TutorialFile;
import com.notes.nicefact.service.CommonEntityService;
import com.notes.nicefact.service.GoogleDriveService;
import com.notes.nicefact.service.TutorialService;
import com.notes.nicefact.to.FileTO;
import com.notes.nicefact.to.GoogleDriveFile;
import com.notes.nicefact.util.EntityManagerHelper;
import com.notes.nicefact.util.Utils;

public class UpdateThumbnailTutorialServerFiles implements Job {

	static Logger logger = Logger.getLogger(UpdateThumbnailTutorialServerFiles.class.getSimpleName());
	
    @Override
    public void execute(final JobExecutionContext ctx)
            throws JobExecutionException {
    	logger.info("UpdateThumbnailTutorialServerFiles start");
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			int offset = 0;
			TutorialService tutorialService = new TutorialService(em);
			CommonEntityService commonService = new CommonEntityService(em);
			GoogleDriveService driveService = GoogleDriveService.getInstance();
			/*fetch files with drive file upload id , these are files in service account*/
			List<TutorialFile> files = tutorialService.getPostFilesWithTempDriveId(offset);
			logger.info("files size : " + files.size() + ", at offset : " +offset);
			offset += files.size();
			GoogleDriveFile driveFile;

			do {
				for (TutorialFile postFile : files) {
					if (StringUtils.isBlank(postFile.getThumbnail())) {
						logger.info("processing , " + postFile.getName() + " , " + postFile.getMimeType() + " , " + postFile.getTempGoogleDriveId());
						/*fetch thumbnail file*/
						driveFile = driveService.getFileFieldsServiceAccount(postFile.getTempGoogleDriveId(), "thumbnailLink");
						if (null != driveFile && StringUtils.isNotBlank(driveFile.getThumbnailLink())) {
							HttpResponse httpResponse = driveService.makeServiceAccountGetRequest(driveFile.getThumbnailLink(), null);
							if (null != httpResponse && httpResponse.getStatusLine().getStatusCode() == 200 && httpResponse.getEntity() != null) {
								/*save thumbnail file in local storage and udpate database*/
								byte[] thumbnailBytes = IOUtils.toByteArray(httpResponse.getEntity().getContent());
								FileTO fileTo = Utils.writeTutorialFileThumbnail(thumbnailBytes, postFile.getCreatedBy(), postFile.getName());
								postFile.setThumbnail(fileTo.getServerName());
								postFile.setTempGoogleDriveId(null);
								commonService.upsert(postFile);
								driveService.deleteFileServiceAccount(driveFile.getId());
								offset--;
							}
						}else if((new Date().getTime() -  postFile.getCreatedTime().getTime() )> (24*60*60*1000l) ){
							/* stop trying to fetch thumbnail if it has been more than 24 hours */
							postFile.setTempGoogleDriveId(null);
							commonService.upsert(postFile);
							offset--;
						}
					} else {
						postFile.setTempGoogleDriveId(null);
						commonService.upsert(postFile);
						offset--;
					}
				}
				files = tutorialService.getPostFilesWithTempDriveId(offset);
				logger.info("files size : " + files.size() + ", at offset : " +offset);
			} while (!files.isEmpty());

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
		logger.info("exit UpdateThumbnailTutorialServerFiles");
    }


}
