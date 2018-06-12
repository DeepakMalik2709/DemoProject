package com.notes.nicefact.quiz.service;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import com.notes.nicefact.dao.CommonDAO;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.quiz.dao.StudentQuizDao;
import com.notes.nicefact.quiz.entity.StudentQuiz;
import com.notes.nicefact.quiz.to.StudentQuizTO;
import com.notes.nicefact.service.BackendTaskService;
import com.notes.nicefact.service.CommonService;

public class StudentQuizService extends CommonService<StudentQuiz> {
	private final static Logger logger = Logger.getLogger(StudentQuizService.class.getName());
	BackendTaskService backendTaskService;
	QuizService quizService;
	StudentQuizDao studentQuizDao;
	EntityManager em;
	
	public StudentQuizService(EntityManager em) {
		this.em = em;
		backendTaskService = new BackendTaskService(em);
		quizService = new QuizService(em);
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


	public StudentQuiz upsertStudentQuiz(StudentQuizTO studentQuizTO, AppUser user) {
		StudentQuiz studentquizDB=null;		
		if (null == studentQuizTO.getId() || studentQuizTO.getId() <= 0) {
			studentquizDB = new StudentQuiz();			
		} else {
			studentquizDB = studentQuizDao.get(studentQuizTO.getId());
		}
		studentquizDB.setQuiz(quizService.get(studentQuizTO.getQuizTO().getId()));
		studentquizDB.setStudent(user);

		studentquizDB =studentQuizDao.upsert(studentquizDB);
		logger.info("upsertQuiz : ");
		return studentquizDB;
	}

}
