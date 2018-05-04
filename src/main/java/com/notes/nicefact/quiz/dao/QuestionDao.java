/**
 * 
 */
package com.notes.nicefact.quiz.dao;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import com.notes.nicefact.dao.impl.CommonDAOImpl;
import com.notes.nicefact.quiz.entity.Question;

/**
 * @author user
 *
 */
public class QuestionDao extends CommonDAOImpl<Question> {

	static Logger logger = Logger.getLogger(QuestionDao.class.getSimpleName());

	
	public QuestionDao(EntityManager em) {
		super(em);
		// TODO Auto-generated constructor stub
	}



}
