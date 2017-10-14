package com.notes.nicefact.service;

import java.io.IOException;
import java.io.InputStream;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import com.notes.nicefact.content.AllSchoolError;
import com.notes.nicefact.content.AllSchoolException;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.PostFile;
import com.notes.nicefact.to.GoogleDriveFile;

public class LibraryService {
	private final static Logger logger = Logger.getLogger(LibraryService.class.getName());
	BackendTaskService backendTaskService ;
	PostService postService;
	public LibraryService(EntityManager em) {
		backendTaskService = new BackendTaskService(em);
		postService = new PostService(em);
	}

	public GoogleDriveFile addToLibrary(String serverName, AppUser user) throws IOException, AllSchoolException {
		GoogleDriveFile driveFile = null;
		GoogleDriveService driveService = GoogleDriveService.getInstance();
		PostFile postFile = postService.getByServerName(serverName);
		if (postFile == null) {
			throw new AllSchoolException(AllSchoolError.FILE_NOT_AVAILABLE_CODE,AllSchoolError.FILE_NOT_AVAILABLE_MESSAGE); 			
		}		
		byte[] bytes = driveService.downloadFile(postFile,user);
		if(bytes!=null){
			driveFile = driveService.uploadInputStreamFile(postFile,user,bytes);
		}else{
			throw new AllSchoolException(AllSchoolError.FILE_NOT_AVAILABLE_CODE,AllSchoolError.FILE_NOT_AVAILABLE_MESSAGE); 
		}
		
		return driveFile;
		
	}

	
	
}
