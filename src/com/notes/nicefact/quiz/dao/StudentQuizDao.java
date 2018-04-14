/**
 * 
 */
package com.notes.nicefact.quiz.dao;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import com.notes.nicefact.dao.impl.CommonDAOImpl;
import com.notes.nicefact.quiz.entity.StudentQuiz;

/**
 * @author user
 *
 */
public class StudentQuizDao extends CommonDAOImpl<StudentQuiz> {

	static Logger logger = Logger.getLogger(StudentQuizDao.class.getSimpleName());

	
	public StudentQuizDao(EntityManager em) {
		super(em);
		// TODO Auto-generated constructor stub
	}



}
