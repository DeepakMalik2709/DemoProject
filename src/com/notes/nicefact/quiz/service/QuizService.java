package com.notes.nicefact.quiz.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import com.notes.nicefact.dao.CommonDAO;
import com.notes.nicefact.dao.PostRecipientDAO;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.Group;
import com.notes.nicefact.exception.ServiceException;
import com.notes.nicefact.quiz.dao.QuizDao;
import com.notes.nicefact.quiz.entity.Question;
import com.notes.nicefact.quiz.entity.Quiz;
import com.notes.nicefact.quiz.to.QuizTO;
import com.notes.nicefact.service.BackendTaskService;
import com.notes.nicefact.service.CommonService;
import com.notes.nicefact.service.GroupService;
import com.notes.nicefact.to.SearchTO;

public class QuizService extends CommonService<Quiz> {
	private final static Logger logger = Logger.getLogger(QuizService.class.getName());
	BackendTaskService backendTaskService;
	GroupService groupService;
	QuestionService questionService;
	PostRecipientDAO postRecipientDAO;
	QuizDao quizDao;
	EntityManager em;
	
	public QuizService(EntityManager em) {
		this.em = em;
		backendTaskService = new BackendTaskService(em);
		groupService = new GroupService(em);
		questionService = new QuestionService(em);
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
			quizDB.updateProps(quizTO);
		}
		for (Long groupId : quizTO.getGroups()) {
			Group grp = groupService.get(groupId);
			quizDB.getGroups().add(grp);
		}
		for(Long quesId : quizTO.getQuestions()) {
			Question ques = questionService.get(quesId);
			quizDB.getQuestions().add(ques);
		}
		quizDao.upsert(quizDB);
		logger.info("upsertQuiz : ");
		return quizDB;
	}


	public List<QuizTO> fetchMyQuiz(SearchTO searchTO, AppUser user) {
		List<Quiz> quizs = quizDao.getQuizForGroups("group", user.getGroupIds(), searchTO.getFirst(), searchTO.getLimit() ,null );
		List<QuizTO> quizTOs = new ArrayList<QuizTO>();
		
		for (Quiz quiz : quizs) {
			quizTOs.add(new QuizTO(quiz));
		}
		return quizTOs;
	}


}
