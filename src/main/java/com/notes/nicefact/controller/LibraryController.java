package com.notes.nicefact.controller;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.service.LibraryService;
import com.notes.nicefact.to.FileTO;
import com.notes.nicefact.to.GoogleDriveFile;
import com.notes.nicefact.util.Constants;
import com.notes.nicefact.util.EntityManagerHelper;

@Path("/library")
public class LibraryController extends CommonController {

	private final static Logger logger = Logger.getLogger(LibraryController.class.getName());
	
	
	@POST
	@Path("/addToLibrary")
	@Consumes(MediaType.APPLICATION_JSON)
	public void addToLibrary(FileTO fileTO, @Context HttpServletResponse response,@Context HttpServletRequest request) {
		logger.info("deleteGroupPost start");
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			AppUser user = (AppUser) request.getSession().getAttribute(Constants.SESSION_KEY_lOGIN_USER);
			LibraryService libService = new LibraryService(em);				
			GoogleDriveFile driveFile = libService.addToLibrary(fileTO.getServerName(),user);			
			json.put(Constants.DATA_ITEM, driveFile);
			json.put(Constants.CODE, Constants.RESPONSE_OK);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);

			json.put(Constants.CODE, Constants.ERROR_WITH_MSG);
			json.put(Constants.MESSAGE, e.getMessage());
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
		renderResponseJson(json, response);
		logger.info("uploadPostFile exit");
	    
	}	
	
}
