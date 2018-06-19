package com.notes.nicefact.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;

import com.notes.nicefact.comparator.CreatedDateComparator;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.Group;
import com.notes.nicefact.entity.Post;
import com.notes.nicefact.enums.SHARING;
import com.notes.nicefact.exception.NotFoundException;
import com.notes.nicefact.exception.UnauthorizedException;
import com.notes.nicefact.service.AppUserService;
import com.notes.nicefact.service.PostService;
import com.notes.nicefact.to.PostTO;
import com.notes.nicefact.to.SearchTO;
import com.notes.nicefact.util.CacheUtils;
import com.notes.nicefact.util.Constants;
import com.notes.nicefact.util.CurrentContext;
import com.notes.nicefact.util.EntityManagerHelper;

@Path("/secure")
public class PostController extends CommonController {

	private final static Logger logger = Logger.getLogger(PostController.class);
	
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
	
	@GET
	@Path("/post/{postId}")
	public void fetchPost(@PathParam("postId") long postId, @Context HttpServletResponse response, @Context HttpServletRequest request) {
		logger.info("fetchPost start, postId : " + postId);
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
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
}
