package com.notes.nicefact.quiz.service;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import com.notes.nicefact.dao.CommonDAO;
import com.notes.nicefact.dao.PostRecipientDAO;
import com.notes.nicefact.quiz.dao.StudentQuizDao;
import com.notes.nicefact.quiz.entity.StudentQuiz;
import com.notes.nicefact.service.BackendTaskService;
import com.notes.nicefact.service.CommonService;
import com.notes.nicefact.service.GroupService;
import com.notes.nicefact.service.PostService;

public class StudentQuizService extends CommonService<StudentQuiz> {
	private final static Logger logger = Logger.getLogger(StudentQuizService.class.getName());
	BackendTaskService backendTaskService;
	GroupService groupService;
	PostService postService;
	PostRecipientDAO postRecipientDAO;
	StudentQuizDao studentQuizDao;
	EntityManager em;
	
	public StudentQuizService(EntityManager em) {
		this.em = em;
		backendTaskService = new BackendTaskService(em);
		groupService = new GroupService(em);
		postService = new PostService(em);
		postRecipientDAO = new PostRecipientDAO(em);
		studentQuizDao = new StudentQuizDao(em);
	}


	@Override
	protected CommonDAO<StudentQuiz> getDAO() {
		return studentQuizDao;
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
