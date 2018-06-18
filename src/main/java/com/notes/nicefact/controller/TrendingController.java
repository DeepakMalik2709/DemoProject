package com.notes.nicefact.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;

import com.notes.nicefact.entity.Post;
import com.notes.nicefact.service.PostService;
import com.notes.nicefact.to.PostTO;
import com.notes.nicefact.to.SearchTO;
import com.notes.nicefact.to.TrendingTO;
import com.notes.nicefact.util.Constants;
import com.notes.nicefact.util.EntityManagerHelper;

@Path("/public/trending")
public class TrendingController extends CommonController {
	
	private final static Logger logger = Logger.getLogger(TrendingController.class.getName());
	
	@GET
	@Path("/")
	public void  fetchTrending(@Context HttpServletRequest request,   @Context HttpServletResponse response){
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		logger.info("searchTutorial start");
		Map<String, Object> json = new HashMap<>();
		try {
			SearchTO searchTO = new SearchTO(request, Constants.RECORDS_5);
			
			PostService postService = new PostService(em);
			List<Post> postList = postService.getAllPublicPost(searchTO);
			List<TrendingTO> trendingTOList = new ArrayList<>();
			
			for(Post post: postList) {
				trendingTOList.add(new TrendingTO(post));
			}
			
			json.put(Constants.CODE, Constants.RESPONSE_OK);
			json.put(Constants.TOTAL, trendingTOList.size());
			json.put(Constants.DATA_ITEMS, trendingTOList);
			if(!trendingTOList.isEmpty()){
				json.put(Constants.NEXT_LINK, searchTO.getNextLink());
			}
			
			/*
			TutorialService notesService = new TutorialService(em);
			List<Tutorial> tutorials =  notesService.fetchTrendingTutorialList(searchTO);
			List<TutorialTO> tutorialTos = Utils.adaptTutorialTO(tutorials);
			json.put(Constants.CODE, Constants.RESPONSE_OK);
			json.put(Constants.TOTAL, tutorialTos.size());
			json.put(Constants.DATA_ITEMS, tutorialTos);
			if(!tutorialTos.isEmpty()){
				json.put(Constants.NEXT_LINK, searchTO.getNextLink());
			}
			*/
		} catch (Exception e) {
			logger.error( e.getMessage(), e);
			
			json.put(Constants.CODE, Constants.ERROR_WITH_MSG);
			json.put(Constants.MESSAGE, e.getMessage());
		}finally{
			if(em.isOpen()){
				em.close();
			}
		}
		renderResponseJson(json, response);
		logger.info("searchTutorial exit");
	}
	
	@GET
	@Path("/search")
	public void  searchTutorial(@QueryParam("q") String searchTerm, @QueryParam("offset") int offset,@Context HttpServletRequest request,   @Context HttpServletResponse response){
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		logger.info("searchTutorial start");
		Map<String, Object> json = new HashMap<>();
		try {
			SearchTO searchTO = new SearchTO(request, Constants.RECORDS_20);
			
			PostService postService = new PostService(em);
			List<Post> postList = postService.searchPublicPost(searchTO);
			List<TrendingTO> trendingTOList = new ArrayList<>();
			
			for(Post post: postList) {
				trendingTOList.add(new TrendingTO(post));
			}
			/*
			TutorialService notesService = new TutorialService(em);
			SearchTO searchTO = new SearchTO(request, Constants.RECORDS_20);
			List<Tutorial> tutorials =  notesService.search(searchTO);
			List<TutorialTO> tutorialTos = Utils.adaptTutorialTO(tutorials);
			*/
			json.put(Constants.CODE, Constants.RESPONSE_OK);
			json.put(Constants.TOTAL, trendingTOList.size());
			json.put(Constants.DATA_ITEMS, trendingTOList);
			if(!trendingTOList.isEmpty()){
				json.put(Constants.NEXT_LINK, searchTO.getNextLink());
			}
			
		} catch (Exception e) {
			logger.error( e.getMessage(), e);
			
			json.put(Constants.CODE, Constants.ERROR_WITH_MSG);
			json.put(Constants.MESSAGE, e.getMessage());
		}finally{
			if(em.isOpen()){
				em.close();
			}
		}
		renderResponseJson(json, response);
		logger.info("searchTutorial exit");
	}

}
