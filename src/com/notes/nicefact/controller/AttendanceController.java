package com.notes.nicefact.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;

import com.notes.nicefact.comparator.CreatedDateComparator;
import com.notes.nicefact.service.PostService;
import com.notes.nicefact.to.PostTO;
import com.notes.nicefact.to.SearchTO;
import com.notes.nicefact.util.Constants;
import com.notes.nicefact.util.EntityManagerHelper;

@Path("/attendance")
public class AttendanceController extends CommonController {

	private final static Logger logger = Logger.getLogger(AttendanceController.class.getName());

	@GET
	@Path("/group/{groupId}/groupAttendance")
	public void fetchGroupPosts(@PathParam("groupId") long groupId, @Context HttpServletRequest request, @Context HttpServletResponse response) {
		logger.info("fetchGroupPosts start");
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			PostService postService = new PostService(em);
			SearchTO searchTO = new SearchTO(request, Constants.RECORDS_20);
			searchTO.setGroupId(groupId);
			List<PostTO> postTos = postService.search(searchTO);		
			Collections.sort(postTos, new CreatedDateComparator());
			json.put(Constants.CODE, Constants.RESPONSE_OK);
			json.put(Constants.TOTAL, postTos.size());
			json.put(Constants.DATA_ITEMS, postTos);
			if (!postTos.isEmpty() ) {
				json.put(Constants.NEXT_LINK, searchTO.getNextLink());
			}
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
		logger.info("fetchGroupPosts exit");
	}

}
