package com.notes.nicefact.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.google.api.services.drive.model.File;
import com.notes.nicefact.content.AllSchoolError;
import com.notes.nicefact.service.LibraryService;
import com.notes.nicefact.util.Constants;

@Path("/library")
public class LibraryController extends CommonController {

	private final static Logger logger = Logger.getLogger(LibraryController.class.getName());
	LibraryService libService = new LibraryService();
	
	@POST
	@Path("/moveFileToFolder")
	@Consumes(MediaType.APPLICATION_JSON)
	public void moveFileToFolder(com.notes.nicefact.entity.Book book, @Context HttpServletResponse response,@Context HttpServletRequest request) {
		Map<String, Object> json = new HashMap<>();
		try {
			logger.info("schedule : "+book);
			File file =	libService.moveFileToFolder(book.getFileId(),book.getFolderId());				
			if(file !=null){			
				json.put(Constants.CODE, Constants.OK);
				json.put(Constants.MESSAGE,"Successfully added to your library.");
			}else{
				json.put(Constants.CODE, AllSchoolError.GOOGLE_CALENDAR_AUTHORIZATION_NULL_CODE	);
				json.put(Constants.MESSAGE, AllSchoolError.GOOGLE_CALENDAR_AUTHORIZATION_NULL_MESSAGE);	
			}
		} catch (IOException e) {
			json.put(Constants.CODE, Constants.ERROR_WITH_MSG);
			json.put(Constants.MESSAGE, e.getMessage());	
			e.printStackTrace();
		}
		renderResponseJson(json, response);
		logger.info("getPostGroupOrder exit");
	    
	}
	
	@POST
	@Path("/copyFile")
	@Consumes(MediaType.APPLICATION_JSON)
	public void copyFile(com.notes.nicefact.entity.Book book, @Context HttpServletResponse response,@Context HttpServletRequest request) {
		Map<String, Object> json = new HashMap<>();
		try {
			File file =	libService.copyFile(book.getFileId(),book.getFolderId());			
			logger.info("schedule : "+book);			
			if(file !=null){
				json.put(Constants.CODE, Constants.OK);
				json.put(Constants.MESSAGE,"Successfully added to your library.");
			}else{
				json.put(Constants.CODE, AllSchoolError.GOOGLE_CALENDAR_AUTHORIZATION_NULL_CODE	);
				json.put(Constants.MESSAGE, AllSchoolError.GOOGLE_CALENDAR_AUTHORIZATION_NULL_MESSAGE);	
			}
		} catch (IOException e) {
			json.put(Constants.CODE, Constants.ERROR_WITH_MSG);
			json.put(Constants.MESSAGE, e.getMessage());	
			e.printStackTrace();
		}
		renderResponseJson(json, response);
		logger.info("getPostGroupOrder exit");
	    
	}
	
	
	
}
