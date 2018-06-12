/**
 * 
 */
package com.notes.nicefact.quiz.dao;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import com.notes.nicefact.dao.impl.CommonDAOImpl;
import com.notes.nicefact.quiz.entity.Option;

/**
 * @author kuldeep joshi
 *
 */
public class OptionDao extends CommonDAOImpl<Option> {

	static Logger logger = Logger.getLogger(OptionDao.class.getSimpleName());

	
	public OptionDao(EntityManager em) {
		super(em);
		// TODO Auto-generated constructor stub
	}



}
