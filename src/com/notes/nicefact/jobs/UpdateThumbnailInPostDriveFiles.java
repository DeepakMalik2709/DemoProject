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

import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.PostFile;
import com.notes.nicefact.service.CommonEntityService;
import com.notes.nicefact.service.GoogleDriveService;
import com.notes.nicefact.service.PostService;
import com.notes.nicefact.to.FileTO;
import com.notes.nicefact.to.GoogleDriveFile;
import com.notes.nicefact.util.CacheUtils;
import com.notes.nicefact.util.EntityManagerHelper;
import com.notes.nicefact.util.Utils;

public class UpdateThumbnailInPostDriveFiles implements Job {

	static Logger logger = Logger.getLogger(UpdateThumbnailInPostDriveFiles.class.getSimpleName());
	
    @Override
    public void execute(final JobExecutionContext ctx)
            throws JobExecutionException {
    	logger.info("UpdateThumbnailInPostDriveFiles start");
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			int offset = 0;
			PostService postService = new PostService(em);
			CommonEntityService commonService = new CommonEntityService(em);
			GoogleDriveService driveService = GoogleDriveService.getInstance();
			/*fetch files with drive file upload id , these are files in service account*/
			List<PostFile> files = postService.getDrivePostFilesWithoutThumbnail(offset);
			logger.info("files size : " + files.size() + ", at offset : " +offset);
			offset += files.size();
			GoogleDriveFile driveFile;
			AppUser user;
			do {
				for (PostFile postFile : files) {
						logger.info("processing , " + postFile.getName() + " , " + postFile.getMimeType() + " , " + postFile.getTempGoogleDriveId());
						/*fetch thumbnail file*/
						user = CacheUtils.getAppUser(postFile.getCreatedBy());
						driveFile = driveService.getFileFields(postFile.getGoogleDriveId(), "thumbnailLink", user);
						if (null != driveFile && StringUtils.isNotBlank(driveFile.getThumbnailLink())) {
							HttpResponse httpResponse = driveService.makeServiceAccountGetRequest(driveFile.getThumbnailLink(), null);
							if (null != httpResponse && httpResponse.getStatusLine().getStatusCode() == 200 && httpResponse.getEntity() != null) {
								/*save thumbnail file in local storage and udpate database*/
								byte[] thumbnailBytes = IOUtils.toByteArray(httpResponse.getEntity().getContent());
								FileTO fileTo = Utils.writeGroupPostFileThumbnail(thumbnailBytes, postFile.getPost().getGroupId(), postFile.getName());
								postFile.setThumbnail(fileTo.getServerName());
								commonService.upsert(postFile);
								offset--;
							}
						}
				}
				files = postService.getPostFilesWithTempDriveId(offset);
				logger.info("files size : " + files.size() + ", at offset : " +offset);
			} while (!files.isEmpty());

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}

		logger.info("exit UpdateThumbnailInPostDriveFiles");

    }


}
