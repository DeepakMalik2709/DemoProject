package com.notes.nicefact.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.fileupload.MultipartStream;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;
import org.glassfish.jersey.server.mvc.Viewable;
import org.json.JSONException;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.notes.nicefact.comparator.CreatedDateComparator;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.Group;
import com.notes.nicefact.entity.GroupMember;
import com.notes.nicefact.entity.Post;
import com.notes.nicefact.entity.PostComment;
import com.notes.nicefact.entity.PostFile;
import com.notes.nicefact.entity.Tag;
import com.notes.nicefact.entity.TaskSubmission;
import com.notes.nicefact.entity.Tutorial;
import com.notes.nicefact.enums.SHARING;
import com.notes.nicefact.exception.NotFoundException;
import com.notes.nicefact.exception.UnauthorizedException;
import com.notes.nicefact.google.GoogleAppUtils;
import com.notes.nicefact.service.AppUserService;
import com.notes.nicefact.service.BackendTaskService;
import com.notes.nicefact.service.CommonEntityService;
import com.notes.nicefact.service.GroupService;
import com.notes.nicefact.service.NotificationService;
import com.notes.nicefact.service.PostService;
import com.notes.nicefact.service.TagService;
import com.notes.nicefact.service.TaskService;
import com.notes.nicefact.service.TutorialService;
import com.notes.nicefact.to.AppUserTO;
import com.notes.nicefact.to.CommentTO;
import com.notes.nicefact.to.FileTO;
import com.notes.nicefact.to.GroupChildrenTO;
import com.notes.nicefact.to.GroupMemberTO;
import com.notes.nicefact.to.GroupTO;
import com.notes.nicefact.to.NotificationTO;
import com.notes.nicefact.to.PostTO;
import com.notes.nicefact.to.SearchTO;
import com.notes.nicefact.to.TagTO;
import com.notes.nicefact.to.TaskSubmissionTO;
import com.notes.nicefact.to.TutorialTO;
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
			if(StringUtils.isBlank(user.getRefreshTokenAccountEmail())){
				if(null == user.getGoogleDriveMsgDate() ||  ((new Date().getTime() - user.getGoogleDriveMsgDate().getTime())   > (1*24*60*60*1000) )){
					EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
					AppUserService appUserService = new AppUserService(em);
					AppUser dbUser = appUserService.getAppUserByEmail(user.getEmail());
					dbUser.setGoogleDriveMsgDate(new Date());
					appUserService.upsert(dbUser);
					req.getSession().setAttribute(Constants.SESSION_KEY_lOGIN_USER, dbUser);
					em.close();
				}
			}
			
			
			Map<String, Object> userMap = user.toMap();
			json.put(Constants.LOGIN_USER, userMap);
			json.put(Constants.CONTEXT, CurrentContext.getCommonContext().toMap());
			CurrentContext.getCommonContext().setMessage(null);
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
	@Path("/dashboard")
	public Viewable dashboard(@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception {
		return new Viewable("/jsp/index.jsp", null);
	}

	@POST
	@Path("/profile")
	@Consumes(MediaType.APPLICATION_JSON)
	public void upsertProfile(AppUserTO appUserTO, @Context HttpServletResponse response) {
		logger.info("upsertProfile start");
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		Map<String, Object> json = new HashMap<>();
		try {
			AppUserService appUserService = new AppUserService(em);
			AppUser updated = appUserService.updateAppUser(CurrentContext.getEmail(), appUserTO);
			json.put(Constants.CODE, Constants.RESPONSE_OK);
			json.put(Constants.DATA_ITEM, updated.toMap());

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
		logger.info("upsertProfile exit");
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

	@DELETE
	@Path("/group/{groupId}/members/{memberId}")
	public void deleteGroupMember(@Context HttpServletResponse response, @Context HttpServletRequest request, @PathParam("groupId") long groupId, @PathParam("memberId") long memberId) {
		logger.info("start : deleteGroupMember");
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
	@Path("/group/{groupId}/members/{memberId}/toggleAdmin")
	public void toggleGroupAdmin(@Context HttpServletResponse response, @Context HttpServletRequest request, @PathParam("groupId") long groupId, @PathParam("memberId") long memberId,
			@QueryParam("isAdmin") boolean isAdmin) {
		logger.info("start : toggleGroupAdmin");
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			GroupService groupService = new GroupService(em);
			GroupMember member = groupService.toggleGroupAdmin(groupId, memberId, isAdmin);
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
		logger.info("exit : toggleGroupAdmin");
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

	@POST
	@Path("/profile" + Constants.PHOTO_URL)
	public void uploadUserImage(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException, JSONException {
		logger.info("uploadUserImage start");
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			String contentType = request.getContentType();
			String boundary = contentType.substring(contentType.indexOf("boundary=") + 9);
			MultipartStream multipartStream = new MultipartStream(request.getInputStream(), boundary.getBytes(Constants.UTF_8));
			boolean nextPart = multipartStream.skipPreamble();
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			while (nextPart) {
				String header = multipartStream.readHeaders();
				/* process headers , DO NOT REMOVE , UPLOAD STOPS WORKING */
				System.out.println(header);
				multipartStream.readBodyData(output);
				nextPart = multipartStream.readBoundary();
			}

			byte[] fileBytes = output.toByteArray();
			AppUserService appUserService = new AppUserService(em);
			AppUser user = appUserService.uploadPhoto(CurrentContext.getEmail(), fileBytes);
			request.getSession().setAttribute(Constants.SESSION_KEY_lOGIN_USER, user);
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
		logger.info("uploadUserImage exit");
	}

	@DELETE
	@Path("/profile" + Constants.PHOTO_URL)
	public void removeUserImage(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException, JSONException {
		logger.info("removeUserImage start");
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			AppUserService appUserService = new AppUserService(em);
			AppUser user = appUserService.removePhoto(CurrentContext.getEmail());
			request.getSession().setAttribute(Constants.SESSION_KEY_lOGIN_USER, user);

			json.put(Constants.CODE, Constants.RESPONSE_OK);
			AppUserTO userTO = new AppUserTO(user);
			json.put(Constants.LOGIN_USER, userTO);
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
		renderResponseJson(json, response);
		logger.info("removeUserImage exit");

	}

	@DELETE
	@Path("/group/{groupId}/post/{postId}")
	public void deleteGroupPost(@PathParam("groupId") long groupId, @PathParam("postId") long postId, @Context HttpServletResponse response) {
		logger.info("deleteGroupPost start , postId : " + postId + ", groupId" + groupId);
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			PostService postService = new PostService(em);
			postService.deletePost(groupId, postId, CurrentContext.getAppUser());
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

	@DELETE
	@Path("/post/{postId}/comment/{commentId}")
	public void deleteGroupPostComment(@PathParam("postId") long postId, @PathParam("commentId") long commentId, @Context HttpServletResponse response) {
		logger.info("deleteGroupPostComment start , postId : " + postId + " , commentId : " + commentId);
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			PostService postService = new PostService(em);
			postService.deletePostComment(postId, commentId, CurrentContext.getAppUser());
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
		logger.info("deleteGroupPostComment exit");
	}

	@POST
	@Path("/post/{postId}/react")
	public void reactToPost(@PathParam("postId") long postId, @Context HttpServletResponse response) {
		logger.info("reactToPost start , postId : " + postId);
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			PostService postService = new PostService(em);
			Post post = postService.reactToPost(postId, CurrentContext.getAppUser());
			json.put(Constants.CODE, Constants.RESPONSE_OK);
			json.put(Constants.DATA_ITEM, post.getNumberOfReactions());
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
		logger.info("reactToPost exit");
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
	@Path("/my/posts")
	public void fetchMyPosts(@PathParam("groupId") long groupId, @Context HttpServletRequest request, @Context HttpServletResponse response) {
		logger.info("fetchMyPosts start");
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			AppUser user = CurrentContext.getAppUser();
			PostService postService = new PostService(em);
			SearchTO searchTO = new SearchTO(request, Constants.RECORDS_20);
			searchTO.setGroupId(groupId);
			List<PostTO> postTos = postService.fetchMyPosts(searchTO, user);
			try {
				com.google.api.services.calendar.Calendar service = GoogleAppUtils.getCalendarService();
				// List the next 10 events from the primary calendar.
				if (service != null) {
					Events events = service.events().list("primary").execute();
					List<Event> items = events.getItems();
					if (items.size() > 0) {
						for (Event event : items) {
							PostTO postto = new PostTO(event, user);
							postto.setId(RandomUtils.nextLong());
							postTos.add(postto);
						}
					}

				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
			Collections.sort(postTos, new CreatedDateComparator());
			if (postTos.isEmpty()) {
				json.put(Constants.CODE, Constants.NO_RESULT);
			} else {

				json.put(Constants.DATA_ITEMS, postTos);
				json.put(Constants.NEXT_LINK, searchTO.getNextLink());
				json.put(Constants.CODE, Constants.RESPONSE_OK);
				json.put(Constants.TOTAL, postTos.size());
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
		logger.info("fetchMyPosts exit");
	}

	@GET
	@Path("/group/file/thumbnail")
	public void generateFileThumbnail(@QueryParam("name") String serverName, @Context HttpServletRequest req, @Context HttpServletResponse response) {
		logger.info("generateFileThumbnail start");
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
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
					}
				} 
			}
			if (null == fileBytes) {
				response.sendRedirect(Constants.NO_PREVIEW_IMAGE);
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
	
	@POST
	@Path("/profile/deauthorizeGoogleDrive" )
	public void deauthorizeGoogleDrive(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException, JSONException {
		logger.info("deauthorizeGoogleDrive start");
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			AppUserService appUserService = new AppUserService(em);
			AppUser user = appUserService.deauthorizeGoogleDrive(CurrentContext.getEmail());
			request.getSession().setAttribute(Constants.SESSION_KEY_lOGIN_USER, user);

			json.put(Constants.CODE, Constants.RESPONSE_OK);
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
		renderResponseJson(json, response);
		logger.info("deauthorizeGoogleDrive exit");

	}
	
	
	@POST
	@Path("/profile/deauthorizeGoogleCalendar" )
	public void deauthorizeGoogleCalendar(@Context HttpServletRequest request, @Context HttpServletResponse response) throws IOException, JSONException {
		logger.info("deauthorizeGoogleDrive start");
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			AppUserService appUserService = new AppUserService(em);
			AppUser user = appUserService.deauthorizeGoogleDrive(CurrentContext.getEmail());
			request.getSession().setAttribute(Constants.SESSION_KEY_lOGIN_USER, user);

			json.put(Constants.CODE, Constants.RESPONSE_OK);
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
		renderResponseJson(json, response);
		logger.info("deauthorizeGoogleDrive exit");

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
			List<NotificationTO> notifications = notificationService.fetchMyNotifications(CurrentContext.getAppUser(), searchTO);
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
	@Path("/post/{postId}")
	public void fetchPost(@PathParam("postId") long postId, @Context HttpServletResponse response, @Context HttpServletRequest request) {
		logger.info("fetchPost start, postId : " + postId);
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			GroupService groupService = new GroupService(em);
			PostService postService = new PostService(em);
			Post post = postService.get(postId);
			AppUser user = CurrentContext.getAppUser();
			if (null == post){
				throw new NotFoundException("Post not found for id : " + postId);
			}
			Group group = CacheUtils.getGroup(post.getGroupId());
			if(group == null){
				throw new NotFoundException("Post has been deleted");
			}
			if(!SHARING.PUBLIC.equals(group.getSharing())){
				if (!user.getGroupIds().contains(post.getGroupId())) {
					AppUserService appUserService = new AppUserService(em);
					user = appUserService.getAppUserByEmail(user.getEmail());
					if (!user.getGroupIds().contains(post.getGroupId())) {
						throw new UnauthorizedException("Post not found");
					}
					request.getSession().setAttribute(Constants.SESSION_KEY_lOGIN_USER, user);
				}
			}
			
			PostTO postTO = new PostTO(post);
			json.put(Constants.CODE, Constants.RESPONSE_OK);
			json.put(Constants.DATA_ITEM, postTO);
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
		logger.info("fetchPost exit");
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
	

	@POST
	@Path("task/submission")
	public void taskSubmission(TaskSubmissionTO sumbmissionTO, @Context HttpServletRequest request, @Context HttpServletResponse response) {
		logger.info("taskSubmission start");
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			TaskService taskService = new TaskService(em);
			TaskSubmission post = taskService.upsertTaskSubmission(sumbmissionTO, CurrentContext.getAppUser());
			TaskSubmissionTO savedTO = new TaskSubmissionTO(post);
			json.put(Constants.CODE, Constants.RESPONSE_OK);
			/*json.put(Constants.DATA_ITEM, savedTO);*/
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
	
}
