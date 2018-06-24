package com.notes.nicefact.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import com.notes.nicefact.dao.AppUserDAO;
import com.notes.nicefact.dao.TagDAO;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.Tag;
import com.notes.nicefact.enums.SEARCH_INFO;
import com.notes.nicefact.response.SearchInfoResponse;
import com.notes.nicefact.to.SearchInfoTO;
import com.notes.nicefact.util.Constants;
import com.notes.nicefact.util.CurrentContext;

public class SearchInfoService {

	private static Logger logger = Logger.getLogger(SearchInfoService.class.getSimpleName());

	private AppUserDAO appUserDAO;
	private TagDAO tagDAO;
	
	public SearchInfoService(EntityManager em) {
		appUserDAO = new AppUserDAO(em);
		tagDAO = new TagDAO(em);
	}
	
	public Map<String, List<SearchInfoResponse>> search(SearchInfoTO searchInfoTO) {
		Map<String, List<SearchInfoResponse>> searchInfoResponseMap = new HashMap<>();
		
		AppUser loggedInUser = CurrentContext.getAppUser();
		
		SearchInfoResponse searchInfoResponse;
		
		// Getting searched person's data
		if(searchInfoTO.getSearchIn().equals(SEARCH_INFO.ALL.name()) 
				|| searchInfoTO.getSearchIn().equals(SEARCH_INFO.PEOPLE.name())) {
			
			List<SearchInfoResponse> searchInfoResponses = new ArrayList<>();
			List<AppUser> users = appUserDAO.getAllUserExceptLoggedIn(searchInfoTO, loggedInUser.getId());
			
			if(users.size() > 0) {
				for(AppUser user: users) {
					searchInfoResponse = new SearchInfoResponse();
					searchInfoResponse.setId(user.getId());
					searchInfoResponse.setName(user.getDisplayName());
					
					searchInfoResponses.add(searchInfoResponse);
				}
				
				searchInfoResponseMap.put(Constants.PEOPLES, searchInfoResponses);
			}
		}
		
		// Getting search hashtags detail
		if(searchInfoTO.getSearchIn().equals(SEARCH_INFO.ALL.name()) 
				|| searchInfoTO.getSearchIn().equals(SEARCH_INFO.HASHTAG.name())) {
			
			List<SearchInfoResponse> searchInfoResponses = new ArrayList<>();
			List<Tag> hashtags = tagDAO.search(searchInfoTO);
			
			if(hashtags.size() > 0) {
				for(Tag hashtag: hashtags) {
					searchInfoResponse = new SearchInfoResponse();
					searchInfoResponse.setId(hashtag.getId());
					searchInfoResponse.setName(hashtag.getName());
					searchInfoResponse.setDescription(hashtag.getDescription());
					
					searchInfoResponses.add(searchInfoResponse);
				}
				
				searchInfoResponseMap.put(Constants.HASH_TAGS, searchInfoResponses);
			}
		}
		
		return searchInfoResponseMap;
	}
}
