package com.notes.nicefact.quiz.service;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import com.notes.nicefact.dao.CommonDAO;
import com.notes.nicefact.dao.PostRecipientDAO;
import com.notes.nicefact.quiz.dao.OptionDao;
import com.notes.nicefact.quiz.entity.Option;
import com.notes.nicefact.service.BackendTaskService;
import com.notes.nicefact.service.CommonService;
import com.notes.nicefact.service.GroupService;
import com.notes.nicefact.service.PostService;

public class OptionService extends CommonService<Option> {
	private final static Logger logger = Logger.getLogger(OptionService.class.getName());
	BackendTaskService backendTaskService;
	GroupService groupService;
	PostService postService;
	PostRecipientDAO postRecipientDAO;
	OptionDao optionDao;
	EntityManager em;
	
	public OptionService(EntityManager em) {
		this.em = em;
		backendTaskService = new BackendTaskService(em);
		groupService = new GroupService(em);
		postService = new PostService(em);
		postRecipientDAO = new PostRecipientDAO(em);
		optionDao = new OptionDao(em);
	}

	@Override
	protected CommonDAO<Option> getDAO() {
		return optionDao;
	}

	public BackendTaskService getBackendTaskService() {
		return backendTaskService;
	}

	public void setBackendTaskService(BackendTaskService backendTaskService) {
		this.backendTaskService = backendTaskService;
	}

	public GroupService getGroupService() {
		return groupService;
	}

	public void setGroupService(GroupService groupService) {
		this.groupService = groupService;
	}


}
