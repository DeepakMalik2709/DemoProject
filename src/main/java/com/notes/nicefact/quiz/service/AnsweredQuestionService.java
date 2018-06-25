package com.notes.nicefact.quiz.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

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
		answeredQuestionDB.setQuiz(quizService.get(answeredQuestionTO.getQuizId()));
		answeredQuestionDB.setQuestion(questionService.get(answeredQuestionTO.getQuestionId()));
		answeredQuestionDB.setOption(optionService.get(answeredQuestionTO.getOptionId()));
		answeredQuestionDB.setStudent(user);

		answeredQuestionDB =answeredQuestionDao.upsert(answeredQuestionDB);
		logger.info("upsertQuiz : ");
		return answeredQuestionDB;
	}

	public List<AnsweredQuestionTO> queryRecord(AnsweredQuestionTO answeredQuestionTO, AppUser user) {
		Map<String,Object> queryMap = new  HashMap<>();
		if(answeredQuestionTO.getQuizId()!=null) {
			queryMap.put("quiz_id", answeredQuestionTO.getQuizId());
		}
		List<AnsweredQuestionTO> ansQuesTOList = new ArrayList<>();
		List<AnsweredQuestion> ansQuesList = answeredQuestionDao.getActiveListByMap(queryMap);
		for (AnsweredQuestion answeredQuestion : ansQuesList) {
			ansQuesTOList.add(new AnsweredQuestionTO(answeredQuestion));
		}
		return ansQuesTOList;
	}


}
