
package com.notes.nicefact.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.glassfish.jersey.server.mvc.Viewable;
import org.json.JSONException;

import com.notes.nicefact.comparator.CreatedDateComparator;
import com.notes.nicefact.dao.GroupMemberDAO;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.Certificate;
import com.notes.nicefact.entity.Group;
import com.notes.nicefact.entity.GroupAttendance;
import com.notes.nicefact.entity.GroupMember;
import com.notes.nicefact.entity.Institute;
import com.notes.nicefact.entity.InstituteMember;
import com.notes.nicefact.entity.Post;
import com.notes.nicefact.entity.PostComment;
import com.notes.nicefact.entity.PostFile;
import com.notes.nicefact.entity.Tag;
import com.notes.nicefact.entity.TaskSubmission;
import com.notes.nicefact.entity.Tutorial;
import com.notes.nicefact.enums.UserPosition;
import com.notes.nicefact.exception.UnauthorizedException;
import com.notes.nicefact.service.AppUserService;
import com.notes.nicefact.service.BackendTaskService;
import com.notes.nicefact.service.CommonEntityService;
import com.notes.nicefact.service.GroupAttendanceService;
import com.notes.nicefact.service.GroupService;
import com.notes.nicefact.service.InstituteService;
import com.notes.nicefact.service.NotificationService;
import com.notes.nicefact.service.PostService;
import com.notes.nicefact.service.PushService;
import com.notes.nicefact.service.TagService;
import com.notes.nicefact.service.TaskService;
import com.notes.nicefact.service.TutorialService;
import com.notes.nicefact.service.profile.CertificateService;
import com.notes.nicefact.to.CommentTO;
import com.notes.nicefact.to.FileTO;
import com.notes.nicefact.to.GroupAttendanceTO;
import com.notes.nicefact.to.GroupChildrenTO;
import com.notes.nicefact.to.GroupMemberTO;
import com.notes.nicefact.to.GroupTO;
import com.notes.nicefact.to.InstituteMemberTO;
import com.notes.nicefact.to.InstituteTO;
import com.notes.nicefact.to.NotificationTO;
import com.notes.nicefact.to.PostTO;
import com.notes.nicefact.to.SearchTO;
import com.notes.nicefact.to.TagTO;
import com.notes.nicefact.to.TaskSubmissionTO;
import com.notes.nicefact.to.TutorialTO;
import com.notes.nicefact.to.profile.CertificateTo;
import com.notes.nicefact.util.AppProperties;
import com.notes.nicefact.util.CacheUtils;
import com.notes.nicefact.util.Constants;
import com.notes.nicefact.util.CurrentContext;
import com.notes.nicefact.util.EntityManagerHelper;
import com.notes.nicefact.util.Utils;

@Path("/secure")
public class SecureController extends CommonController {

	private final static Logger logger = Logger.getLogger(SecureController.class);

	@GET
	@Path("/context")
	public void fetchContext(@Context HttpServletRequest req, @Context HttpServletResponse response) {
		Map<String, Object> json = new HashMap<>();
		json.put(Constants.CODE, Constants.RESPONSE_OK);
		AppUser user = CurrentContext.getAppUser();
		if (null != user) {
			EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
			try {
				if (StringUtils.isBlank(user.getRefreshTokenAccountEmail())) {
					if (null == user.getGoogleDriveMsgDate() || ((new Date().getTime() - user.getGoogleDriveMsgDate().getTime()) > (1 * 24 * 60 * 60 * 1000))) {
						AppUserService appUserService = new AppUserService(em);
						AppUser dbUser = appUserService.getAppUserByEmail(user.getEmail());
						dbUser.setGoogleDriveMsgDate(new Date());
						appUserService.upsert(dbUser);
						req.getSession().setAttribute(Constants.SESSION_KEY_lOGIN_USER, dbUser);
					}
				}
				List<InstituteMember> instituteMemberList = Utils.getInstitutesFromSession(req.getSession());
				if (null == instituteMemberList) {
					InstituteService instituteService = new InstituteService(em);
					instituteMemberList = instituteService.fetchJoinedInstituteMembers(user.getEmail());
					Utils.detachInstituteMembers(instituteMemberList);
					req.getSession().setAttribute(Constants.SESSION_INSTITUTES, instituteMemberList);
				}
				List<InstituteTO> institutes = new ArrayList<>();
				List<InstituteMemberTO> instituteMembers = new ArrayList<>();
				if(instituteMemberList.isEmpty()){
					if (null == user.getAddInstituteMsgDate() || ((new Date().getTime() - user.getAddInstituteMsgDate().getTime()) > (1 * 24 * 60 * 60 * 1000))) {
						AppUserService appUserService = new AppUserService(em);
						AppUser dbUser = appUserService.getAppUserByEmail(user.getEmail());
						dbUser.setAddInstituteMsgDate(new Date());
						appUserService.upsert(dbUser);
						req.getSession().setAttribute(Constants.SESSION_KEY_lOGIN_USER, dbUser);
					}
				}else{
					InstituteTO instituteTO;
					InstituteMemberTO instituteMemberTO;
					for (InstituteMember member : instituteMemberList) {
						instituteTO = new InstituteTO(member);
						institutes.add(instituteTO);
						
						instituteMemberTO = new InstituteMemberTO(member);
						instituteMembers.add(instituteMemberTO);
					}
				}
				
				List<CertificateTo> certificates = new ArrayList<>();
				
				CertificateService certificateService = new CertificateService(em);
				List<Certificate> certificates2 = certificateService.getByAppUserId(user.getId());
				
				for(Certificate certificate: certificates2) {
					certificates.add(CertificateTo.convert(certificate));
				}
				
				json.put(Constants.CERTIFICATES, certificates);
				
				json.put(Constants.SESSION_INSTITUTES, institutes);
				json.put(Constants.SESSION_INSTITUTE_MEMBERS, instituteMembers);
				Map<String, Object> pushToken = PushService.getInstance().getToken();
				json.put("pushToken" , pushToken);
				Map<String, Object> userMap = user.toMap();
				userMap.put("haveEditAccess", true);
				json.put(Constants.LOGIN_USER, userMap);
				json.put(Constants.APPLICATION_URL, AppProperties.getInstance().getApplicationUrl());
				json.put(Constants.CONTEXT, CurrentContext.getCommonContext().toMap());
				CurrentContext.getCommonContext().setMessage(null);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				json.put(Constants.CODE, Constants.ERROR_WITH_MSG);
				json.put(Constants.MESSAGE, e.getMessage());
			} finally {
				if (em.isOpen()) {
					em.close();
				}
			}
		}

		renderResponseJson(json, response);
	}

	@GET
	@Path("/tutorial/create")
	public Viewable publicHome(@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception {
		return new Viewable("/jsp/tutorial/create.jsp", null);
	}

	@POST
	@Path("/tutorial/upsert")
	@Consumes(MediaType.APPLICATION_JSON)
	public void upsertTutorial(TutorialTO tutorialTO, @Context HttpServletResponse response) {
		logger.info("upsertTutorial start");
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		Map<String, Object> json = new HashMap<>();
		try {
			TutorialService notesService = new TutorialService(em);
			Tutorial tutorial = notesService.upsert(tutorialTO);
			TutorialTO savedTO = new TutorialTO(tutorial);
			json.put(Constants.DATA_ITEM, savedTO);
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
		logger.info("upsertTutorial exit");
	}

	@DELETE
	@Path("/tutorial/{id}")
	public void deleteTutorial(@PathParam("id") long id, @Context HttpServletResponse response) {
		logger.info("deleteTutorial start");
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		Map<String, Object> json = new HashMap<>();
		try {
			TutorialService notesService = new TutorialService(em);
			Tutorial tutorial = notesService.get(id);
			if (tutorial.getCreatedBy().equals(CurrentContext.getEmail())) {
				notesService.remove(id);
			}
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
		logger.info("deleteTutorial exit");
	}

	@DELETE
	@Path("/group/{id}")
	public void deleteGroup(@PathParam("id") long id, @Context HttpServletResponse response) {
		logger.info("deleteGroup start");
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			GroupService groupService = new GroupService(em);
			Group tutorial = CacheUtils.getGroup(id);
			if (tutorial.getCreatedBy().equals(CurrentContext.getEmail())) {
				groupService.remove(id);
			}

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
		logger.info("deleteGroup exit");
	}

	@GET
	@Path("/tutorial/myList")
	public void fetchMyTutorialList(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		logger.info("fetchTutorialList start");
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		Map<String, Object> json = new HashMap<>();
		try {
			TutorialService notesService = new TutorialService(em);
			SearchTO searchTO = new SearchTO(request, Constants.RECORDS_5);
			List<Tutorial> tutorials = notesService.fetchMyTutorialList(searchTO);
			List<TutorialTO> tutorialTos = Utils.adaptTutorialTO(tutorials);
			json.put(Constants.CODE, Constants.RESPONSE_OK);
			json.put(Constants.TOTAL, tutorialTos.size());
			json.put(Constants.DATA_ITEMS, tutorialTos);
			if (!tutorialTos.isEmpty()) {
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
		logger.info("fetchTutorialList exit");
	}
	
	@GET
	@Path("/groups/mine")
	public void listMyGroups(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		logger.info("listMyGroups start");
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			GroupService groupService = new GroupService(em);
			SearchTO searchTO = new SearchTO(request, Constants.RECORDS_40);
			List<GroupTO> groups = groupService.fetchMyGroups(searchTO, CurrentContext.getAppUser());
			json.put(Constants.CODE, Constants.RESPONSE_OK);
			json.put(Constants.DATA_ITEMS, groups);
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
		logger.info("listMyGroups exit");
	}

	@POST
	@Path("/group/upsert")
	@Consumes(MediaType.APPLICATION_JSON)
	public void upsertGroup(GroupTO groupTO, @Context HttpServletResponse response) {
		logger.info("upsertGroup start");
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			GroupService groupService = new GroupService(em);
			Group group = groupService.upsert(groupTO, CurrentContext.getAppUser());
			GroupTO savedTO = new GroupTO(group, false);
			json.put(Constants.CODE, Constants.RESPONSE_OK);
			json.put(Constants.DATA_ITEM, savedTO);
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
		logger.info("upsertGroup exit");
	}

	@GET
	@Path("/group/{groupId}")
	public void fetchGroup(@PathParam("groupId") long groupId, @Context HttpServletResponse response, @Context HttpServletRequest request) {
		logger.info("fetchGroup start, groupId : " + groupId);
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			GroupService groupService = new GroupService(em);
			AppUser user = CurrentContext.getAppUser();
			if (!user.getGroupIds().contains(groupId)) {
				AppUserService appUserService = new AppUserService(em);
				user = appUserService.getAppUserByEmail(user.getEmail());
				if (!user.getGroupIds().contains(groupId)) {
					throw new UnauthorizedException("User cannot view this group.");
				}
				request.getSession().setAttribute(Constants.SESSION_KEY_lOGIN_USER, user);
			}
			Group group = groupService.get(groupId);
			if (null == group) {
				json.put(Constants.CODE, Constants.NO_RESULT);
			} else {
				CacheUtils.addGroupToCache(group);
				GroupTO savedTO = new GroupTO(group, false);
				boolean isTeacher = group.getTeachers().contains(user.getEmail());
				savedTO.setIsTeacher(isTeacher);
				if(isTeacher){
					if(group.getIsGroupAttendaceAllowed()){
						savedTO.setCanMarkAttendance(true);
					}
				}
				json.put(Constants.CODE, Constants.RESPONSE_OK);
				json.put(Constants.DATA_ITEM, savedTO);
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
		logger.info("fetchGroup exit");
	}

	@POST
	@Path("/group/{groupId}/members")
	@Consumes(MediaType.APPLICATION_JSON)
	public void addGroupMembers(@Context HttpServletResponse response, @Context HttpServletRequest request, @PathParam("groupId") long groupId, GroupChildrenTO members) {
		logger.info("start : addGroupMembers");
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			GroupService groupService = new GroupService(em);
			GroupTO groupTO = groupService.addGroupMembers(groupId, members, CurrentContext.getAppUser());
			json.put(Constants.DATA_ITEM, groupTO);
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
		logger.info("exit : addGroupMembers");
	}

	@GET
	@Path("/group/{groupId}/members")
	public void fetchGroupMembers(@Context HttpServletResponse response, @Context HttpServletRequest request, @PathParam("groupId") long groupId) {
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
	@Path("/group/{groupId}/attendance/members")
	public void fetchGroupAttendanceMembers(@Context HttpServletResponse response, @Context HttpServletRequest request,GroupAttendanceTO groupAttendanceTO, @PathParam("groupId") long groupId) {
		logger.info("start : fetchGroupAttendanceMembers");
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			GroupService groupService = new GroupService(em);
			SearchTO searchTO = SearchTO.getInstances().setRequest(request).setGroupId(groupId).setFromTime(groupAttendanceTO.getFromTime()).setDate(groupAttendanceTO.getDate());
			GroupAttendanceTO attendance  = groupService.fetchGroupAttendanceMembers( searchTO);
			json.put(Constants.DATA_ITEM, attendance);
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
		logger.info("exit : fetchGroupAttendanceMembers");
	}

	@GET
	@Path("/group/{groupId}/attendance")
	@Consumes(MediaType.APPLICATION_JSON)
	public void fetchStudentAttendance(@Context HttpServletRequest request,@Context HttpServletResponse response, @PathParam("groupId") long groupId,
			@QueryParam("fromDate") long fromDate,@QueryParam("toDate") long toDate) {
		logger.info("start : fetchGroupAttendanceMembers");
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			/*Date fromDate = new Date();
			Date toDate = new Date();
			*/
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(fromDate);
			Date start = cal.getTime();
			cal.setTimeInMillis(toDate);
			Date end = cal.getTime();
			AppUser user = CurrentContext.getAppUser();			
			SearchTO searchTO = new SearchTO(request, Constants.RECORDS_40);
			GroupAttendanceService groupAttendenceService = new GroupAttendanceService(em);
			GroupAttendanceTO groupAttendanceTO = groupAttendenceService.fetchStudentAttendance( searchTO,groupId,user.getEmail(),start,end);			
			json.put(Constants.DATA_ITEM, groupAttendanceTO);
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
		logger.info("exit : fetchGroupAttendanceMembers");
	}
	
	@POST
	@Path("/group/{groupId}/attendance/upsert")
	@Consumes(MediaType.APPLICATION_JSON)
	public void upsertGroupAttendance(GroupAttendanceTO groupAttendanceTO, @Context HttpServletResponse response) {
		logger.info("upsertGroupAttendance start");
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			AppUser user = CurrentContext.getAppUser();
			GroupAttendanceService groupAttendenceService = new GroupAttendanceService(em);
			GroupAttendance groupAttendance = groupAttendenceService.upsert(groupAttendanceTO, user);
			GroupAttendanceTO groupAttendanceTO2 = new GroupAttendanceTO(groupAttendance);
			json.put(Constants.DATA_ITEM, groupAttendanceTO2);
			json.put(Constants.CODE, Constants.RESPONSE_OK);
			json.put(Constants.MESSAGE, "Attendance saved.");
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
		logger.info("upsertGroupAttendance exit");
	}
	
	@POST
	@Path("/group/{groupId}/attendance/delete")
	@Consumes(MediaType.APPLICATION_JSON)
	public void deleteGroupAttendance(GroupAttendanceTO groupAttendanceTO, @Context HttpServletResponse response) {
		logger.info("deleteGroupAttendance start");
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			AppUser user = CurrentContext.getAppUser();
			GroupAttendanceService groupAttendenceService = new GroupAttendanceService(em);
			
			groupAttendenceService.deleteAttendance(groupAttendanceTO, user);
			json.put(Constants.CODE, Constants.RESPONSE_OK);
			json.put(Constants.MESSAGE, "Attendance deleted.");
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
		logger.info("deleteGroupAttendance exit");
	}
	
	@DELETE
	@Path("/group/{groupId}/members/{memberId}")
	public void deleteGroupMember(@Context HttpServletResponse response, @Context HttpServletRequest request, @PathParam("groupId") long groupId, @PathParam("memberId") long memberId) {
		logger.info("start : deleteGroupMember , groupId : " + groupId + " , memberId :" + memberId);
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			GroupService groupService = new GroupService(em);
			groupService.deleteGroupMember(groupId, memberId, CurrentContext.getAppUser());
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
		logger.info("exit : deleteGroupMember");
	}

	@DELETE
	@Path("/group/{groupId}/memberGroup/{memberGroupId}")
	public void deleteGroupMemberGroup(@Context HttpServletResponse response, @Context HttpServletRequest request, @PathParam("groupId") long groupId, @PathParam("memberGroupId") long memberGroupId) {
		logger.info("start : deleteGroupMemberGroup");
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			GroupService groupService = new GroupService(em);
			groupService.deleteGroupMemberGroup(groupId, memberGroupId, CurrentContext.getAppUser());
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
		logger.info("exit : deleteGroupMemberGroup");
	}

	@POST
	@Path("/group/{groupId}/member/update")
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateGroupMembers(@Context HttpServletResponse response, @Context HttpServletRequest request,  @PathParam("groupId") long groupId, GroupMemberTO memberTO) {
		logger.info("start : updateGroupMembers");
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			GroupService groupService = new GroupService(em);
			GroupMemberTO member = groupService.updateGroupMember(groupId, memberTO, CurrentContext.getAppUser());
			json.put(Constants.DATA_ITEM, member);
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
		logger.info("exit : updateGroupMembers");
	}

	

	@POST
	@Path("/group/{groupId}/members/{memberId}/toggleBlock")
	public void toggleGroupBlock(@Context HttpServletResponse response, @Context HttpServletRequest request, @PathParam("groupId") long groupId, @PathParam("memberId") long memberId,
			@QueryParam("isBlocked") boolean isBlocked) {
		logger.info("start : toggleGroupBlock");
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			GroupService groupService = new GroupService(em);
			GroupMember member = groupService.toggleGroupBlock(groupId, memberId, isBlocked);
			GroupMemberTO to = new GroupMemberTO(member);
			json.put(Constants.DATA_ITEM, to);
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
		logger.info("exit : toggleGroupBlock");
	}

	@POST
	@Path("/group/post")
	@Consumes(MediaType.APPLICATION_JSON)
	public void upsertGroupPost(PostTO postTo, @Context HttpServletResponse response) {
		logger.info("upsertGroupPost start");
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
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
	
	@POST
	@Path("/group/task")
	@Consumes(MediaType.APPLICATION_JSON)
	public void upsertGroupTask(PostTO postTo, @Context HttpServletResponse response) {
		logger.info("upsertGroupTask start");
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			PostService postService = new PostService(em);
			 List<Post> posts = postService.upsertTask(postTo, CurrentContext.getAppUser());
			 List<PostTO> savedTOs = new ArrayList<>();
			 for(Post post : posts){
				 PostTO savedTO = new PostTO(post);
				 savedTOs.add(savedTO);
			 }
			json.put(Constants.CODE, Constants.RESPONSE_OK);
			json.put(Constants.DATA_ITEMS, savedTOs);
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
		logger.info("upsertGroupTask exit");
	}

	@POST
	@Path("/group/post/comment")
	@Consumes(MediaType.APPLICATION_JSON)
	public void upsertGroupPostComment(CommentTO commentTO, @Context HttpServletResponse response) {
		logger.info("upsertGroupPostComment start");
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			PostService postService = new PostService(em);
			PostComment comment = postService.upsertComment(commentTO, CurrentContext.getAppUser());
			CommentTO savedTO = new CommentTO(comment);
			json.put(Constants.CODE, Constants.RESPONSE_OK);
			json.put(Constants.DATA_ITEM, savedTO);
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
		logger.info("upsertGroupPostComment exit");
	}

	@POST
	@Path("/group/post/comment/reply")
	@Consumes(MediaType.APPLICATION_JSON)
	public void upsertGroupPostCommentReply(CommentTO commentTO, @Context HttpServletResponse response) {
		logger.info("upsertGroupPostCommentReply start");
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			PostService postService = new PostService(em);
			PostComment comment = postService.upsertCommentReply(commentTO, CurrentContext.getAppUser());
			CommentTO savedTO = new CommentTO(comment);
			json.put(Constants.CODE, Constants.RESPONSE_OK);
			json.put(Constants.DATA_ITEM, savedTO);
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
		logger.info("upsertGroupPostCommentReply exit");
	}

	@GET
	@Path("/group/{groupId}/posts")
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

	@DELETE
	@Path("/group/post/{postId}")
	public void deleteGroupPost(@PathParam("postId") long postId, @Context HttpServletResponse response) {
		logger.info("deleteGroupPost start , postId : " + postId);
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			PostService postService = new PostService(em);
			postService.deletePost(postId, CurrentContext.getAppUser());
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
		logger.info("deleteGroupPost exit");
	}

	@POST
	@Path("/uploadFile")
	public void uploadPostFile(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException, JSONException {
		logger.info("uploadPostFile start");
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {

			List<FileTO> files = Utils.writeFilesToTempfolder(request);
			json.put(Constants.DATA_ITEMS, files);
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

	@GET
	@Path("/group/download")
	public void downloadGroupFile(@QueryParam("name") String serverName, @Context HttpServletRequest req, @Context HttpServletResponse response) {
		logger.info("downloadGroupFile start");
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			AppUser user = (AppUser) req.getSession().getAttribute(Constants.SESSION_KEY_lOGIN_USER);
			PostService postService = new PostService(em);
			CommonEntityService commonEntityService = new CommonEntityService(em);
			PostFile postFile = postService.getByServerName(serverName);
			if (postFile == null) {
				renderResponseRaw("File not found", response);
			} else {
				postFile.incrementDownloadCount();
				commonEntityService.upsert(postFile);
				byte[] fileBytes = Utils.readFileBytes(postFile.getPath());
				if (null !=fileBytes) {
					downloadFile(fileBytes, postFile.getName(), postFile.getMimeType(), response);
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
		logger.info("downloadGroupFile exit");
	}

	@GET
	@Path("/tags/search")
	public void tagSearch(@Context HttpServletRequest req, @Context HttpServletResponse response) {
		Map<String, Object> json = new HashMap<>();
		SearchTO searchTO = new SearchTO(req, Constants.RECORDS_20);
		logger.info("tagSearch start , searchTerm : " + searchTO.getSearchTerm());
		if (StringUtils.isNotBlank(searchTO.getSearchTerm())) {
			EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
			try {
				TagService tagService = new TagService(em);
				List<TagTO> tags = tagService.search(searchTO);
				json.put(Constants.DATA_ITEMS, tags);
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
		}
		renderResponseJson(json, response);
		logger.info("tagSearch exit");
	}

	@GET
	@Path("/tags/{id}")
	public void getTagById(@PathParam("id") long id, @Context HttpServletRequest req, @Context HttpServletResponse response) {
		Map<String, Object> json = new HashMap<>();
		logger.info("getTagById start , id : " + id);
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			TagService tagService = new TagService(em);
			Tag tag = tagService.get(id);
			TagTO saved = new TagTO(tag);
			json.put(Constants.DATA_ITEM, saved);
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
		logger.info("tagSearch exit");
	}

	@POST
	@Path("/tags/upsert")
	public void upsertTag(TagTO tagTo, @Context HttpServletRequest req, @Context HttpServletResponse response) {
		Map<String, Object> json = new HashMap<>();
		logger.info("upsertTag start , tagTo : " + tagTo);
		if (StringUtils.isNotBlank(tagTo.getName()) && CurrentContext.getAppUser().getIsSuperAdmin()) {
			EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
			try {
				TagService tagService = new TagService(em);
				Tag tag = tagService.upsert(tagTo);
				TagTO saved = new TagTO(tag);
				json.put(Constants.DATA_ITEM, saved);
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
		}
		renderResponseJson(json, response);
		logger.info("upsertTag exit");
	}
	
	@GET
	@Path("/group/file/thumbnail")
	public void generateFileThumbnail(@QueryParam("name") String serverName, @Context HttpServletRequest req, @Context HttpServletResponse response) {
		logger.info("generateFileThumbnail start");
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		String noPreviewImage = Constants.NO_PREVIEW_AVAILABLE;
		try {
			String cacheKey = CacheUtils.getThumbnailCacheKey(serverName);
			byte[] fileBytes = (byte[]) CacheUtils.getFromCache(cacheKey);
			if (null == fileBytes) {
				PostService postService = new PostService(em);
				PostFile postFile = postService.getByServerName(serverName);
				if (postFile != null) {
					if (StringUtils.isNotBlank(postFile.getThumbnail()) && Files.exists(Paths.get(postFile.getThumbnail()))) {
						fileBytes = Utils.readFileBytes(postFile.getThumbnail());
						CacheUtils.putInCache(cacheKey, fileBytes);
					}else if(StringUtils.isNotBlank(postFile.getGoogleDriveId())){
						noPreviewImage = "https://drive.google.com/thumbnail?sz=w200-h200&id=" + postFile.getGoogleDriveId();
					}else if(postFile.getMimeType().contains("pdf")){
						noPreviewImage = Constants.NO_PREVIEW_PDF;
					}else if(postFile.getMimeType().contains("doc") || postFile.getMimeType().contains("docx")){
						noPreviewImage = Constants.NO_PREVIEW_DOC;
					}else if(postFile.getMimeType().contains("ppt")){
						noPreviewImage = Constants.NO_PREVIEW_PPT;
					}else if(postFile.getMimeType().contains("csv") || postFile.getMimeType().contains("xlsx") || postFile.getMimeType().contains("xls")){
						noPreviewImage = Constants.NO_PREVIEW_EXCEL;
					}else if(postFile.getMimeType().contains("jpg") || postFile.getMimeType().contains("jpeg") || postFile.getMimeType().contains("png")){
						noPreviewImage = Constants.NO_PREVIEW_IMAGE;
					}else{
						noPreviewImage = Constants.NO_PREVIEW_AVAILABLE;
					}
				} 
			}
			if (null == fileBytes) {
				response.sendRedirect(noPreviewImage);
			} else {
				renderImage(fileBytes, response);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
		logger.info("generateFileThumbnail exit");
	}
	
	@GET
	@Path("/notifications")
	public void fetchMyNotifications(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		logger.info("fetchMyNotifications start");
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		Map<String, Object> json = new HashMap<>();
		try {
			NotificationService notificationService = new NotificationService(em);
			SearchTO searchTO = new SearchTO(request, Constants.RECORDS_10);
			AppUser user = CurrentContext.getAppUser();
			List<NotificationTO> notifications = notificationService.fetchMyNotifications(user , searchTO);
			boolean newNotifications = false;
			if(!notifications.isEmpty()){
				if(user.getLastSeenNotificationId() == null){
					newNotifications = true;
				}else if(notifications.get(0).getId() > user.getLastSeenNotificationId()){
					newNotifications = true;
				}
			}
			
			if(newNotifications){
				AppUserService appUserService = new AppUserService(em);
				AppUser dbUser = appUserService.getAppUserByEmail(user.getEmail());
				dbUser.setLastSeenNotificationId(notifications.get(0).getId());
				appUserService.upsert(dbUser);
				request.getSession().setAttribute(Constants.SESSION_KEY_lOGIN_USER, dbUser);
			}
			json.put("newNotifications", newNotifications);
			json.put(Constants.CODE, Constants.RESPONSE_OK);
			json.put(Constants.DATA_ITEMS, notifications);
			if (!notifications.isEmpty()) {
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
		logger.info("fetchMyNotifications exit");
	}
	
	@GET
	@Path("/markNotificationAsRead")
	public void markNotificationAsRead(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		logger.info("markNotificationAsRead start");
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		Map<String, Object> json = new HashMap<>();
		try {
			BackendTaskService backendTaskService = new BackendTaskService(em);
			backendTaskService.createMarkNotificationReadTask(CurrentContext.getEmail());
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
		logger.info("markNotificationAsRead exit");
	}
	
	/* task methods start */
	
	@POST
	@Path("task/submission")
	public void taskSubmission(TaskSubmissionTO sumbmissionTO, @Context HttpServletRequest request, @Context HttpServletResponse response) {
		logger.info("taskSubmission start");
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			TaskService taskService = new TaskService(em);
			TaskSubmission taskSumission = taskService.upsertTaskSubmission(sumbmissionTO, CurrentContext.getAppUser());
			TaskSubmissionTO savedTO = new TaskSubmissionTO(taskSumission);
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
		logger.info("taskSubmission exit");
	}
	
	@POST
	@Path("task/resubmission")
	public void taskResubmission(TaskSubmissionTO sumbmissionTO, @Context HttpServletRequest request, @Context HttpServletResponse response) {
		logger.info("taskSubmission start");
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		AppUser appUser = CurrentContext.getAppUser();
		try {
			TaskService taskService = new TaskService(em);
			taskService.deleteTaskSubmission(sumbmissionTO.getPostId(), sumbmissionTO.getId(), appUser);
			TaskSubmission taskSumission = taskService.upsertTaskSubmission(sumbmissionTO, appUser);
			TaskSubmissionTO savedTO = new TaskSubmissionTO(taskSumission);
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
		logger.info("taskSubmission exit");
	}
	
	@GET
	@Path("task/{taskId}/submissions/download")
	public void downlaodTaskSubmission(@PathParam("taskId") long taskId, @Context HttpServletRequest request, @Context HttpServletResponse response) {
		logger.info("downlaodTaskSubmission start");
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			TaskService taskService = new TaskService(em);
			String path = taskService.getTaskSubmissionDownloadPath(taskId);
			if(path != null){
				byte[] fileBytes = Utils.readFileBytes(path);
				if (null !=fileBytes) {
					downloadFile(fileBytes, "submissions.zip", "application/zip", response);
				}
			}
			return;
		} catch (Exception e) {
			logger.error(e.getMessage(), e );

		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
		renderResponseRaw("downloaing failed", response);
		logger.info("downlaodTaskSubmission exit");
	}
	
	/* task methods end */
	
	/* institute methods start */
	@POST
	@Path("/institute/upsert")
	@Consumes(MediaType.APPLICATION_JSON)
	public void upsertInstitute(InstituteTO instituteTO, @Context HttpServletResponse response, @Context HttpServletRequest request) {
		logger.info("upsertInstitute start");
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			InstituteService instituteService = new InstituteService(em);
			Institute institute = instituteService.upsert(instituteTO, CurrentContext.getAppUser());
			InstituteMember member = instituteService.fetchInstituteMember(institute.getId(), CurrentContext.getEmail());
			InstituteTO savedTO = new InstituteTO(member);
			json.put(Constants.CODE, Constants.RESPONSE_OK);
			json.put(Constants.DATA_ITEM, savedTO);
			request.getSession().setAttribute(Constants.SESSION_KEY_lOGIN_USER, CurrentContext.getAppUser());
			request.getSession().removeAttribute(Constants.SESSION_INSTITUTES);
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
		logger.info("upsertInstitute exit");
	}



	@POST
	@Path("/institute/{instituteId}/members")
	@Consumes(MediaType.APPLICATION_JSON)
	public void addInstituteMembers(@Context HttpServletResponse response, @Context HttpServletRequest request, @PathParam("instituteId") long instituteId, GroupChildrenTO members) {
		logger.info("start : addInstituteMembers");
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			InstituteService instituteService = new InstituteService(em);
			InstituteTO instituteTO = instituteService.addInstituteMembers(instituteId, members, CurrentContext.getAppUser());
			json.put(Constants.DATA_ITEM, instituteTO);
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
		logger.info("exit : addInstituteMembers");
	}
	
	@POST
	@Path("/institute/{instituteId}/member/update")
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateInstituteMembers(@Context HttpServletResponse response, @Context HttpServletRequest request, @PathParam("instituteId") long instituteId, GroupMemberTO memberTO) {
		logger.info("start : updateInstituteMembers");
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			InstituteService instituteService = new InstituteService(em);
			GroupMemberTO instituteTO = instituteService.updateInstituteMember(instituteId, memberTO, CurrentContext.getAppUser());
			json.put(Constants.DATA_ITEM, instituteTO);
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
		logger.info("exit : updateInstituteMembers");
	}

	@POST
	@Path("/institute/{instituteId}/join")
	public void joinInstitute(@PathParam("instituteId") long instituteId, @Context HttpServletResponse response, @Context HttpServletRequest request) {
		logger.info("joinInstitute start, instituteId : " + instituteId);
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			InstituteService instituteService = new InstituteService(em);
			AppUser user = CacheUtils.getAppUser( CurrentContext.getEmail());
			InstituteMember member = instituteService.joinInstitute(instituteId, user);
			if (null == member) {
				json.put(Constants.CODE, Constants.NO_RESULT);
			} else {
				GroupMemberTO memberTO = new GroupMemberTO(member);
				json.put(Constants.CODE, Constants.RESPONSE_OK);
				json.put(Constants.DATA_ITEM, memberTO);
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
		logger.info("joinInstitute exit");
	}
	
	@POST
	@Path("/institute/{instituteId}/approveJoin")
	public void joinInstituteApprove(@PathParam("instituteId") long instituteId, GroupMemberTO memberTO1, @Context HttpServletResponse response, @Context HttpServletRequest request) {
		logger.info("joinInstituteApprove start, instituteId : " + instituteId);
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			InstituteService instituteService = new InstituteService(em);
			AppUser user = CacheUtils.getAppUser( CurrentContext.getEmail());
			InstituteMember member = instituteService.approveJoinInstitute(instituteId,memberTO1,  user);
			if (null == member) {
				json.put(Constants.CODE, Constants.NO_RESULT);
			} else {
				GroupMemberTO memberTO = new GroupMemberTO(member);
				json.put(Constants.CODE, Constants.RESPONSE_OK);
				json.put(Constants.DATA_ITEM, memberTO);
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
		logger.info("joinInstituteApprove exit");
	}
	
	@GET
	@Path("/institute/{instituteId}/joinRequests")
	public void fetchInstituteJoinRequests(@Context HttpServletResponse response, @Context HttpServletRequest request, @PathParam("instituteId") long instituteId) {
		logger.info("start : fetchInstituteJoinRequests, instituteId : " + instituteId);
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			InstituteService instituteService = new InstituteService(em);
			SearchTO searchTO = new SearchTO(request, Constants.RECORDS_40);
			List<GroupMemberTO> members = instituteService.fetchInstituteJoinRequests(instituteId, searchTO);
			json.put(Constants.DATA_ITEMS, members);
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
		logger.info("exit : fetchInstituteJoinRequests");
	}

	
	@GET
	@Path("/institute/{instituteId}/members")
	public void fetchInstituteMembers(@Context HttpServletResponse response, @Context HttpServletRequest request, @PathParam("instituteId") long instituteId) {
		logger.info("start : fetchInstituteMembers, instituteId : " + instituteId);
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			InstituteService instituteService = new InstituteService(em);
			SearchTO searchTO = new SearchTO(request, Constants.RECORDS_40);
			List<GroupMemberTO> members = instituteService.fetchInstituteMembers(instituteId, searchTO);
			json.put(Constants.DATA_ITEMS, members);
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
		logger.info("exit : fetchInstituteMembers");
	}

	@DELETE
	@Path("/institute/{instituteId}/members/{memberId}")
	public void deleteInstituteMember(@Context HttpServletResponse response, @Context HttpServletRequest request, @PathParam("instituteId") long instituteId, @PathParam("memberId") long memberId) {
		logger.info("start : deleteInstituteMember, instituteId : " + instituteId + " , memberId :" + memberId);
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			InstituteService instituteService = new InstituteService(em);
			instituteService.deleteInstituteMember(instituteId, memberId, CurrentContext.getAppUser());
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
		logger.info("exit : deleteInstituteMember");
	}


	@POST
	@Path("/institute/{instituteId}/members/{memberId}/toggleBlock")
	public void toggleInstituteBlock(@Context HttpServletResponse response, @Context HttpServletRequest request, @PathParam("instituteId") long instituteId, @PathParam("memberId") long memberId,
			@QueryParam("isBlocked") boolean isBlocked) {
		logger.info("start : toggleInstituteBlock");
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			InstituteService instituteService = new InstituteService(em);
			InstituteMember member = instituteService.toggleInstituteBlock(instituteId, memberId, isBlocked);
			GroupMemberTO to = new GroupMemberTO(member);
			json.put(Constants.DATA_ITEM, to);
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
		logger.info("exit : toggleInstituteBlock");
	}
	
	@GET
	@Path("/institute/search")
	public void searchInstitutes(@Context HttpServletResponse response, @Context HttpServletRequest request) {
		logger.info("start : searchInstitutes ");
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			InstituteService instituteService = new InstituteService(em);
			SearchTO searchTO = new SearchTO(request, Constants.RECORDS_40);
			List<Institute> institutesList = instituteService.searchInstitutes( searchTO);
			List<InstituteTO> institutes = new ArrayList<>();
			InstituteTO instituteTO;
			for (Institute institute : institutesList) {
				instituteTO = new InstituteTO(institute);
				institutes.add(instituteTO);
			}
			
			json.put(Constants.DATA_ITEMS, institutes);
			json.put(Constants.CODE, Constants.RESPONSE_OK);
			if (!institutes.isEmpty()) {
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
		logger.info("exit : searchInstitutes");
	}
	
	
	@POST
	@Path("/instituteMembers/save")
	@Consumes(MediaType.APPLICATION_JSON)
	public void saveInstituteMembersInformation(InstituteMemberTO instituteMemberTO, @Context HttpServletResponse response,
			@Context HttpServletRequest request) {
		logger.info("Save InstituteMember Information start");

		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			InstituteService instituteService = new InstituteService(em);
			
			InstituteMember currentMember =  instituteService.updateInstituteMemberInformation(instituteMemberTO);
			
			List<InstituteMember> instituteMembers = Utils.getInstitutesFromSession(request.getSession());
			
			List<InstituteMember> updatedList = new ArrayList<>();
			for(InstituteMember member: instituteMembers){
				if(currentMember.getId() ==  member.getId()){
					currentMember.getInstitute().getAdmins();
					currentMember.getInstitute().getBlocked();
					
					updatedList.add(currentMember);
				}else{
					member.getInstitute().getAdmins();
					member.getInstitute().getBlocked();
					updatedList.add(member);
				}
			}
			
			request.getSession().setAttribute(Constants.SESSION_INSTITUTES, updatedList);
			
			json.put(Constants.CODE, Constants.RESPONSE_OK);
			json.put(Constants.DATA_ITEM, instituteMemberTO);
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
		logger.info("Save InstituteMember Information exit");
	}
	
	
	/* institute methods end */
	
	
	@GET
	@Path("/updateGroupMemberPositions")
	public void updateGroupMemberPositions(@Context HttpServletResponse response, @Context HttpServletRequest request) {
		logger.info("start : updateGroupMemberPositions ");
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			GroupMemberDAO groupMemberDAO = new GroupMemberDAO(em);
			List<GroupMember> members = groupMemberDAO.getAll();
			logger.warn("members size : " + members.size());
			int count = 0;
			for (GroupMember m : members) {
				if(m.getPositions().isEmpty()){
					if(m.getIsAdmin()){
						m.getPositions().add(UserPosition.ADMIN);
					}else{
						m.getPositions().add(UserPosition.STUDENT);
					}
					count++;
					groupMemberDAO.upsert(m);
				}
			}
			
			logger.warn("updated members size : " + count);
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
		logger.info("exit : updateGroupMemberPositions");
	}
	
	@POST
	@Path("/group/{groupId}/join")
	public void joinGroup(@PathParam("groupId") long groupId, @Context HttpServletResponse response, @Context HttpServletRequest request) {
		logger.info("joinGroup start, groupId : " + groupId);
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			GroupService groupService = new GroupService(em);
			AppUser user = CacheUtils.getAppUser( CurrentContext.getEmail());
			GroupMember member = groupService.joinGroup(groupId, user);
			if (null == member) {
				json.put(Constants.CODE, Constants.NO_RESULT);
			} else {
				GroupMemberTO memberTO = new GroupMemberTO(member);
				json.put(Constants.CODE, Constants.RESPONSE_OK);
				json.put(Constants.DATA_ITEM, memberTO);
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
		logger.info("joinGroup exit");
	}
	
	@POST
	@Path("/group/{groupId}/approveJoin")
	public void joinGroupApprove(@PathParam("groupId") long groupId, GroupMemberTO memberTO1, @Context HttpServletResponse response, @Context HttpServletRequest request) {
		logger.info("joinGroupApprove start, groupId : " + groupId);
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			GroupService groupService = new GroupService(em);
			AppUser user = CacheUtils.getAppUser( CurrentContext.getEmail());
			GroupMember member = groupService.approveJoinGroup(groupId,memberTO1,  user);
			if (null == member) {
				json.put(Constants.CODE, Constants.NO_RESULT);
			} else {
				GroupMemberTO memberTO = new GroupMemberTO(member);
				json.put(Constants.CODE, Constants.RESPONSE_OK);
				json.put(Constants.DATA_ITEM, memberTO);
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
		logger.info("joinGroupApprove exit");
	}
	
	@GET
	@Path("/group/{groupId}/joinRequests")
	public void fetchGroupJoinRequests(@Context HttpServletResponse response, @Context HttpServletRequest request, @PathParam("groupId") long groupId) {
		logger.info("start : fetchGroupJoinRequests , groupId : " + groupId);
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			GroupService groupService = new GroupService(em);
			SearchTO searchTO = new SearchTO(request, Constants.RECORDS_40);
			List<GroupMemberTO> members = groupService.fetchGroupJoinRequests(groupId, searchTO);
			json.put(Constants.DATA_ITEMS, members);
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
		logger.info("exit : fetchGroupJoinRequests");
	}
	
	@GET
	@Path("/group/{groupId}/updatefiles")
	public void updateGroupFiles(@PathParam("groupId") long groupId,@Context HttpServletResponse response, @Context HttpServletRequest request) {
		logger.info("start : updateGroupMemberPositions, groupId : " + groupId);
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			GroupService groupService = new GroupService(em);
			groupService.updateGroupFolderPermissions(groupId);
			groupService.moveUploadedFilesToGroupFolder(groupId);
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
		json.put(Constants.CODE, Constants.RESPONSE_OK);
		renderResponseJson(json, response);
		logger.info("exit : updateGroupFiles");
	}
	
	@POST
	@Path("/saveFirebaseChannelKey")
	public void updateGroupFiles(@FormParam("key") String key, @Context HttpServletResponse response, @Context HttpServletRequest request) {
		logger.info("start : updateGroupMemberPositions, key : " + key);
		Map<String, Object> json = new HashMap<>();
		EntityManager em = null;
		try {
			if (StringUtils.isBlank(key)) {
				logger.error("firebase key is null ");
			} else {
				AppUser sessionuser = CurrentContext.getAppUser();
				if (!sessionuser.getFirebaseChannelKeys().contains(key)) {
					 em = EntityManagerHelper.getDefaulteEntityManager();
					AppUserService appUserService = new AppUserService(em);
					AppUser user = appUserService.get(sessionuser.getId());
					user.addFirebaseChannelKey(key);
					appUserService.upsert(user);
					request.getSession().setAttribute(Constants.SESSION_KEY_lOGIN_USER, user);
				}
			}
		} finally {
			if (null !=em && em.isOpen()) {
				em.close();
			}
		}
		json.put(Constants.CODE, Constants.RESPONSE_OK);
		renderResponseJson(json, response);
		logger.info("exit : updateGroupFiles");
	}
}
