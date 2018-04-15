/**
 * 
 */
package com.notes.nicefact.quiz.dao;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import com.notes.nicefact.dao.impl.CommonDAOImpl;
import com.notes.nicefact.quiz.entity.AnsweredQuestion;

/**
 * @author user
 *
 */
public class AnsweredQuestionDao extends CommonDAOImpl<AnsweredQuestion> {

	static Logger logger = Logger.getLogger(AnsweredQuestionDao.class.getSimpleName());

	
	public AnsweredQuestionDao(EntityManager em) {
		super(em);
		// TODO Auto-generated constructor stub
	}



}
