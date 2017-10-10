package com.notes.nicefact.dao;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import com.notes.nicefact.dao.impl.CommonDAOImpl;
import com.notes.nicefact.entity.TaskSubmission;

public class TaskSubmissionDAO  extends CommonDAOImpl<TaskSubmission> {
	static Logger logger = Logger.getLogger(TaskSubmissionDAO.class.getSimpleName());

	public TaskSubmissionDAO(EntityManager em) {
		super(em);
	}
	
	
}
