package com.notes.nicefact.service;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.google.api.services.drive.model.File;
import com.notes.nicefact.controller.LibraryController;
import com.notes.nicefact.google.GoogleAppUtils;

public class LibraryService {
	private final static Logger logger = Logger.getLogger(LibraryController.class.getName());
	
	public File moveFileToFolder(String fileId, String folderId) throws IOException {
		File file =null;
		com.google.api.services.drive.Drive service = GoogleAppUtils.getDriveService();
		logger.info("fileId : "+fileId+" folderId : "+folderId);
		if(service !=null){
			file = service.files().get(fileId).setFields("parents").execute();
			StringBuilder previousParents = new StringBuilder();
			for (String parent : file.getParents()) {
			  previousParents.append(parent);
			  previousParents.append(',');
			}
			file = service.files().update(fileId, null)
			    .setAddParents(folderId)
			    .setRemoveParents(previousParents.toString())
			    .setFields("id, parents").execute();
		}
		return file;
		
	}

	public File copyFile(String originFileId, String copyTitle) throws IOException {
		File file =null;
		com.google.api.services.drive.Drive service = GoogleAppUtils.getDriveService();
		logger.info("originFileId : "+originFileId+" copyTitle : "+copyTitle);
		if(service !=null){
		File copiedFile = new File();
		copiedFile.setName(copyTitle);
		file = service.files().copy(originFileId, copiedFile).execute();
		}
	   return file;
	}
	

}
