package com.notes.nicefact.quiz.service;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import com.notes.nicefact.dao.CommonDAO;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.quiz.dao.AnsweredQuestionDao;
import com.notes.nicefact.quiz.entity.AnsweredQuestion;
import com.notes.nicefact.quiz.to.AnsweredQuestionTO;
import com.notes.nicefact.quiz.to.StudentQuizTO;
import com.notes.nicefact.service.BackendTaskService;
import com.notes.nicefact.service.CommonService;

public class AnsweredQuestionService extends CommonService<AnsweredQuestion> {
	private final static Logger logger = Logger.getLogger(AnsweredQuestionService.class.getName());
	BackendTaskService backendTaskService;
	QuizService quizService;
	QuestionService questionService;
	AnsweredQuestionDao answeredQuestionDao;
	OptionService optionService;
	EntityManager em;
	
	public AnsweredQuestionService(EntityManager em) {
		this.em = em;
		backendTaskService = new BackendTaskService(em);
		quizService = new QuizService(em);
		optionService = new OptionService(em);
		questionService = new QuestionService(em);		
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

	public void upsertStudentAnswere(StudentQuizTO studentQuizTO, AppUser user) {
		// TODO Auto-generated method stub
		
	}

	public void upsertStudentQuiz(StudentQuizTO studentQuizTO, AppUser user) {
		// TODO Auto-generated method stub
		
	}

	public AnsweredQuestion upsertAnswereQustions(AnsweredQuestionTO answeredQuestionTO, AppUser user) {
		
		AnsweredQuestion answeredQuestionDB=null;		
		if (null == answeredQuestionTO.getId() || answeredQuestionTO.getId() <= 0) {
			answeredQuestionDB = new AnsweredQuestion();			
		} else {
			answeredQuestionDB = answeredQuestionDao.get(answeredQuestionTO.getId());
		}
		answeredQuestionDB.setQuiz(quizService.get(answeredQuestionTO.getQuizTO().getId()));
		answeredQuestionDB.setQuestion(questionService.get(answeredQuestionTO.getQuestionTO().getId()));
		answeredQuestionDB.setOption(optionService.get(answeredQuestionTO.getOptionTO().getId()));
		answeredQuestionDB.setStudent(user);

		answeredQuestionDB =answeredQuestionDao.upsert(answeredQuestionDB);
		logger.info("upsertQuiz : ");
		return answeredQuestionDB;
	}


}
