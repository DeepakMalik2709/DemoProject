package com.notes.nicefact.controller;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;

import com.notes.nicefact.service.SearchInfoService;
import com.notes.nicefact.to.SearchInfoTO;
import com.notes.nicefact.util.Constants;
import com.notes.nicefact.util.EntityManagerHelper;

@Path("/secure")
public class SearchInfoController  extends CommonController {

	private final static Logger logger = Logger.getLogger(PostController.class);
	
	@GET
	@Path("/search")
	public void search(SearchInfoTO postSearchTO, @Context HttpServletRequest request, @Context HttpServletResponse response) {
		logger.info("Post Search start");
		Map<String, Object> json = new HashMap<>();
		
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			SearchInfoService searchInfoService = new SearchInfoService(em);
			
			json.put(Constants.DATA_ITEMS, searchInfoService.search(postSearchTO));
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
		
		logger.info("Post Search end");
	}
}
