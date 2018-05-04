package com.notes.nicefact.dao;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import com.notes.nicefact.dao.impl.CommonDAOImpl;
import com.notes.nicefact.entity.BackendTask;

public class BackendTaskDAO extends CommonDAOImpl<BackendTask> {
	static Logger logger = Logger.getLogger(BackendTaskDAO.class.getSimpleName());
		
		public BackendTaskDAO(EntityManager em) {
			super(em);
		}
		

}
