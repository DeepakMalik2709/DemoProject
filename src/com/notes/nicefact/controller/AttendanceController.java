package com.notes.nicefact.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import com.notes.nicefact.entity.Post;
import com.notes.nicefact.service.GroupAttendanceService;
import com.notes.nicefact.service.GroupService;
import com.notes.nicefact.service.PostService;
import com.notes.nicefact.service.ScheduleService;
import com.notes.nicefact.to.GroupAttendanceTO;
import com.notes.nicefact.to.GroupMemberTO;
import com.notes.nicefact.to.GroupTO;
import com.notes.nicefact.to.PostTO;
import com.notes.nicefact.to.SearchTO;
import com.notes.nicefact.util.Constants;
import com.notes.nicefact.util.CurrentContext;
import com.notes.nicefact.util.EntityManagerHelper;

@Path("/attendance")
public class AttendanceController extends CommonController {

	private final static Logger logger = Logger.getLogger(AttendanceController.class.getName());

	@GET
	@Path("/group/{groupId}/groupAttendance")
	public void fetchGroupMember(@PathParam("groupId") long groupId, @Context HttpServletRequest request, @Context HttpServletResponse response) {
		logger.info("start : fetchGroupMembers");
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			GroupService groupService = new GroupService(em);
			SearchTO searchTO = new SearchTO(request, Constants.RECORDS_40);
			List<GroupMemberTO> members = groupService.fetchGroupMembers(groupId, searchTO);
			List<GroupTO> memberGroups = groupService.fetchMemberGroups(groupId);
			json.put(Constants.DATA_ITEMS, members);
			json.put(Constants.DATA_MEMBER_GROUPS, memberGroups);
			json.put(Constants.CODE, Constants.RESPONSE_OK);
			if (!members.isEmpty()) {
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
		logger.info("exit : fetchGroupMembers");
	}

	
	@POST
	@Path("/group/upsertAttendance")
	@Consumes(MediaType.APPLICATION_JSON)
	public void upsertGroupPost(GroupAttendanceTO groupAttendanceTO, @Context HttpServletResponse response) {
		logger.info("upsertGroupPost start");
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			GroupAttendanceService groupAttendenceService = new GroupAttendanceService(em);
			PostService postService = new PostService(em);
			Post post = postService.upsert(postTo, CurrentContext.getAppUser());
			PostTO savedTO = new PostTO(post);
			json.put(Constants.CODE, Constants.RESPONSE_OK);
			json.put(Constants.DATA_ITEM, savedTO);
		} catch (Exception e) {
			logger.error(e.getMessage(), e );

			json.put(Constants.CODE, Constants.ERROR_WITH_MSG);
			json.put(Constants.MESSAGE, e.getMessage());
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
		renderResponseJson(json, response);
		logger.info("upsertGroupPost exit");
	}
	
}
