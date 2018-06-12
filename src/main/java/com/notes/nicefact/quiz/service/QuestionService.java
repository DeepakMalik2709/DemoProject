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
import com.notes.nicefact.quiz.dao.QuestionDao;
import com.notes.nicefact.quiz.entity.Option;
import com.notes.nicefact.quiz.entity.Question;
import com.notes.nicefact.quiz.entity.Quiz;
import com.notes.nicefact.quiz.to.OptionTO;
import com.notes.nicefact.quiz.to.QuestionTO;
import com.notes.nicefact.quiz.to.QuizTO;
import com.notes.nicefact.service.BackendTaskService;
import com.notes.nicefact.service.CommonService;
import com.notes.nicefact.service.GroupService;
import com.notes.nicefact.service.PostService;

public class QuestionService extends CommonService<Question> {
	private final static Logger logger = Logger.getLogger(QuestionService.class.getName());
	BackendTaskService backendTaskService;
	GroupService groupService;
	PostService postService;
	PostRecipientDAO postRecipientDAO;
	QuestionDao questionDao;
	OptionService optionService;
	EntityManager em;
	
	public QuestionService(EntityManager em) {
		this.em = em;
		backendTaskService = new BackendTaskService(em);
		groupService = new GroupService(em);
		postService = new PostService(em);
		postRecipientDAO = new PostRecipientDAO(em);
		questionDao = new QuestionDao(em);
		optionService=new OptionService(em);
	}

	public List<QuestionTO> getAllActive(){
		List<Question> questions = questionDao.getAllActive();
		List<QuestionTO> questionTOs=new ArrayList<QuestionTO>();
		for(Question question : questions) {
			questionTOs.add(new QuestionTO(question));
		}
		return questionTOs;
	}

	@Override
	protected CommonDAO<Question> getDAO() {
		return questionDao;
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
	
	public OptionService getOptionService() {
		return optionService;
	}

	public void setOptionService(OptionService optionService) {
		this.optionService = optionService;
	}

	public Question upsertQuestion(QuestionTO questionTO, AppUser user) {	
		Question questionDB=null;		
		if (null == questionTO.getId() || questionTO.getId() <= 0) {
			questionDB = new Question(questionTO);			
		} else {
			questionDB = questionDao.get(questionTO.getId());		
			questionDB.updateProps(questionTO);
		}
		for(OptionTO optionTO : questionTO.getOptions()) {
			Option option =optionService.get(optionTO.getId());
			questionDB.getOptions().add(option);
		}
		questionDB = questionDao.upsert(questionDB);
		logger.info("upsertQuiz : ");
		return questionDB;
	}

}
