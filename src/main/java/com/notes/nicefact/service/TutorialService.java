package com.notes.nicefact.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;
import org.hibernate.service.spi.ServiceException;

import com.notes.nicefact.dao.CommonDAO;
import com.notes.nicefact.dao.TagDAO;
import com.notes.nicefact.dao.TutorialDAO;
import com.notes.nicefact.entity.PostFile;
import com.notes.nicefact.entity.Tag;
import com.notes.nicefact.entity.Tutorial;
import com.notes.nicefact.entity.TutorialFile;
import com.notes.nicefact.to.FileTO;
import com.notes.nicefact.to.SearchTO;
import com.notes.nicefact.to.TutorialTO;
import com.notes.nicefact.util.AppProperties;
import com.notes.nicefact.util.CurrentContext;

public class TutorialService extends CommonService<Tutorial> {
	static Logger logger = Logger.getLogger(TutorialService.class.getSimpleName());
	
	private TutorialDAO tutorialDao;
	private TagDAO tagDao;
	AppUserService appUserService;
	BackendTaskService backendTaskService ;
	
	public TutorialService(EntityManager em) {
		tutorialDao = new TutorialDAO(em);
		tagDao = new TagDAO(em);
		appUserService = new AppUserService(em);
		backendTaskService = new BackendTaskService(em);
	}
	
	
	@Override
	protected CommonDAO<Tutorial> getDAO() {
		return tutorialDao;
	}

	public Tutorial get(Long id) {
		Tutorial tutorial = super.get(id);
		populateTagsInTutorial(tutorial);
		return tutorial;
	}
	public List<Tutorial> search(SearchTO searchTO) {
		List<Tutorial>  tutorials = tutorialDao.search( searchTO);
		populateTagsInTutorials(tutorials);
		return tutorials;
	}
	
	public Tutorial upsert(TutorialTO tutorialTO){
		Tutorial tutorial = new Tutorial(tutorialTO);
		if(tutorialTO.getId() > 0){
			Tutorial tutorialDB =  super.get(tutorialTO.getId());
			if (tutorialDB.getCreatedBy().equals(CurrentContext.getEmail())) {
				tutorialDB.updateProps(tutorial);
				updateAttachedFiles(tutorialDB, tutorialTO);
				upsert(tutorialDB);
			}
			backendTaskService.saveTutorialTask(tutorialDB);
			return tutorialDB;
		}else{
			updateAttachedFiles(tutorial, tutorialTO);
			upsert(tutorial);
			appUserService.addTutorial(CurrentContext.getEmail());
			backendTaskService.saveTutorialTask(tutorial);
			populateTagsInTutorial(tutorial);
			return tutorial;
		}
		
	}
	
	private void updateAttachedFiles(Tutorial tutorial, TutorialTO tutorialTO) {
		try {
			String fileBasePath = AppProperties.getInstance().getTutorialUploadsFolder() + CurrentContext.getEmail();
			if (Files.notExists(Paths.get(fileBasePath))) {
				Files.createDirectories(Paths.get(fileBasePath));
			}
			fileBasePath += "/";
			String serverFilePath ; 
			String tempFilePath ;
			
			/* set of file ids that were not delted on UI */
			Set<Long> filesToKeppIds = new HashSet<>(); 
			for (FileTO fileTO : tutorialTO.getFiles()) {
				if (fileTO.getId() > 0) {
					filesToKeppIds.add(fileTO.getId());
				}
			}
			/* delete files from filesystem and db that were delted on UI */
			for(Iterator<TutorialFile> postFileIter = tutorial.getFiles().iterator();postFileIter.hasNext();){
				TutorialFile postFile = postFileIter.next();
				if(!filesToKeppIds.contains(postFile.getId())){
					Files.deleteIfExists(Paths.get(postFile.getPath()));
					postFileIter.remove();
				}
			}
			
			for (FileTO fileTO : tutorialTO.getFiles()) {
				if (fileTO.getId() <= 0) {
					serverFilePath = fileBasePath + fileTO.getServerName();
					tempFilePath = AppProperties.getInstance().getTempUploadsFolder() + fileTO.getServerName();
					if (Files.exists(Paths.get(tempFilePath))) {
						Files.move(Paths.get(tempFilePath), Paths.get(serverFilePath), StandardCopyOption.REPLACE_EXISTING);
						TutorialFile postFile = new TutorialFile(fileTO, serverFilePath);
						postFile.setTutorial(tutorial);
						tutorial.getFiles().add(postFile);
					} 
				}
			}
		} catch (IOException e) {
			logger.error("error for post Id : " + tutorial.getId() + " , " +  e.getMessage(), e);
		}
	}
	public List<Tutorial> fetchMyTutorialList(SearchTO searchTO) {
		if(searchTO.getEmail() == null){
			throw new ServiceException("User is not logged in.");
		}
		List<Tutorial> list = tutorialDao.fetchMyTutorialList( searchTO);
		populateTagsInTutorials(list);
		return list;
	}


	public List<Tutorial> fetchTrendingTutorialList(SearchTO searchTO) {
		List<Tutorial> list = tutorialDao.fetchTrendingTutorialList( searchTO);
		populateTagsInTutorials(list);
		return list;
	}
	
	public void populateTagsInTutorial(Tutorial tutorial){
		if (null != tutorial) {
			List<Tutorial> tutorials = new ArrayList<>();
			tutorials.add(tutorial);
			populateTagsInTutorials(tutorials);
		}
	}
	
	public void populateTagsInTutorials(List<Tutorial> tutorials){
		Set<Long> tagIds = new HashSet<>();
		for (Tutorial tutorial : tutorials) {
			tagIds.addAll(tutorial.getTagIds());
		}
		if(!tagIds.isEmpty()){
			List<Tag> tags =  tagDao.getByKeys(tagIds);
			for (Tutorial tutorial : tutorials) {
				Set<Tag> thisTags = new HashSet<>();
				for (Tag tag : tags) {
					if(tutorial.getTagIds().contains(tag.getId())){
						thisTags.add(tag);
					}
				}
				tutorial.setTags(thisTags);
			}
		}
	}

	public TutorialFile getByServerName(String serverName) {
		return tutorialDao.getByServerName(serverName);
	}
	
	public List<TutorialFile> getPostFilesWithTempDriveId(int offset) {
		List<TutorialFile> files = tutorialDao.getTutorialFilesWithTempDriveId(offset);
		return files;
	}

	public List<TutorialFile> getDrivePostFilesWithoutThumbnail(int offset) {
		List<TutorialFile> files = tutorialDao.getDriveTutorialFilesWithoutThumbnail(offset);
		return files;
	}
}
