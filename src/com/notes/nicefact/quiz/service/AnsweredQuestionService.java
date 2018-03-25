package com.notes.nicefact.quiz.service;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import com.notes.nicefact.dao.CommonDAO;
import com.notes.nicefact.dao.PostRecipientDAO;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.quiz.dao.AnsweredQuestionDao;
import com.notes.nicefact.quiz.entity.AnsweredQuestion;
import com.notes.nicefact.quiz.to.StudentQuizTO;
import com.notes.nicefact.service.BackendTaskService;
import com.notes.nicefact.service.CommonService;
import com.notes.nicefact.service.GroupService;
import com.notes.nicefact.service.PostService;

public class AnsweredQuestionService extends CommonService<AnsweredQuestion> {
	private final static Logger logger = Logger.getLogger(AnsweredQuestionService.class.getName());
	BackendTaskService backendTaskService;
	GroupService groupService;
	PostService postService;
	PostRecipientDAO postRecipientDAO;
	AnsweredQuestionDao answeredQuestionDao;
	EntityManager em;
	
	public AnsweredQuestionService(EntityManager em) {
		this.em = em;
		backendTaskService = new BackendTaskService(em);
		groupService = new GroupService(em);
		postService = new PostService(em);
		postRecipientDAO = new PostRecipientDAO(em);
		answeredQuestionDao = new AnsweredQuestionDao(em);
	}

	@Override
	protected CommonDAO<AnsweredQuestion> getDAO() {
		return answeredQuestionDao;
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

	public void upsertStudentAnswere(StudentQuizTO studentQuizTO, AppUser user) {
		// TODO Auto-generated method stub
		
	}

	public void upsertStudentQuiz(StudentQuizTO studentQuizTO, AppUser user) {
		// TODO Auto-generated method stub
		
	}


}
