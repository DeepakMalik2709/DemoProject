package com.notes.nicefact.quiz.service;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import com.notes.nicefact.dao.CommonDAO;
import com.notes.nicefact.dao.PostRecipientDAO;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.exception.ServiceException;
import com.notes.nicefact.quiz.dao.QuizDao;
import com.notes.nicefact.quiz.entity.Quiz;
import com.notes.nicefact.quiz.to.QuizTO;
import com.notes.nicefact.service.BackendTaskService;
import com.notes.nicefact.service.CommonService;
import com.notes.nicefact.service.GroupService;
import com.notes.nicefact.service.PostService;

public class QuizService extends CommonService<Quiz> {
	private final static Logger logger = Logger.getLogger(QuizService.class.getName());
	BackendTaskService backendTaskService;
	GroupService groupService;
	PostService postService;
	PostRecipientDAO postRecipientDAO;
	QuizDao quizDao;
	EntityManager em;
	
	public QuizService(EntityManager em) {
		this.em = em;
		backendTaskService = new BackendTaskService(em);
		groupService = new GroupService(em);
		postService = new PostService(em);
		postRecipientDAO = new PostRecipientDAO(em);
		quizDao = new QuizDao(em);
	}


	@Override
	protected CommonDAO<Quiz> getDAO() {
		return quizDao;
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


	public Quiz upsertQuiz(QuizTO quizTO, AppUser user) {
		if (quizTO.getGroups().isEmpty()) {
			throw new ServiceException(" Group id cannot be null");
		}	
		Quiz quizDB=null;
		if (null == quizTO.getId() || quizTO.getId() <= 0) {
			quizDB = new Quiz(quizTO);			
		} else {
			quizDB = quizDao.get(quizTO.getId());			
		}
		quizDao.upsert(quizDB);
		logger.info("upsertQuiz : ");
		return quizDB;
	}


}
