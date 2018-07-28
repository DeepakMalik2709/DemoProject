package com.notes.nicefact.service;

import java.io.IOException;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.notes.nicefact.content.AllSchoolError;
import com.notes.nicefact.content.AllSchoolException;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.PostFile;
import com.notes.nicefact.service.GoogleDriveService.FOLDER;
import com.notes.nicefact.to.GoogleDriveFile;
import com.notes.nicefact.to.MoveFileTO;
import com.notes.nicefact.util.CacheUtils;

public class LibraryService {
	private final static Logger logger = Logger.getLogger(LibraryService.class.getName());
	BackendTaskService backendTaskService;
	PostService postService;

	public LibraryService(EntityManager em) {
		backendTaskService = new BackendTaskService(em);
		postService = new PostService(em);
	}

	public GoogleDriveFile addToLibrary(String serverName, AppUser sessionUser) throws IOException, AllSchoolException {
		logger.info("logger start");
		GoogleDriveFile driveFile = null;
		AppUser user = CacheUtils.getAppUser(sessionUser.getEmail());
		GoogleDriveService driveService = GoogleDriveService.getInstance();
		PostFile postFile = postService.getByServerName(serverName);
		if (postFile == null) {
			throw new AllSchoolException(AllSchoolError.FILE_NOT_AVAILABLE_CODE, AllSchoolError.FILE_NOT_AVAILABLE_MESSAGE);
		}
		if (StringUtils.isBlank(postFile.getGoogleDriveId())) {
			byte[] bytes = driveService.downloadFile(postFile, user);
			if (bytes != null) {
				driveFile = driveService.uploadInputStreamFile(postFile, user, bytes);
			} else {
				throw new AllSchoolException(AllSchoolError.FILE_NOT_AVAILABLE_CODE, AllSchoolError.FILE_NOT_AVAILABLE_MESSAGE);
			}
		} else {
			driveFile = driveService.copyFile(postFile.getGoogleDriveId(), user);
		}
		if (null != driveFile) {
			MoveFileTO moveFileTO = MoveFileTO.getInstances().addFileIds(driveFile.getId()).setFileOwner(user.getEmail()).setGroupId(postFile.getPost().getGroupId()).addParents(FOLDER.Library)
					.setUser(user);
			driveService.moveFile(moveFileTO);
		}

		logger.info("logger exit");
		return driveFile;

	}

}
