package com.notes.nicefact.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.notes.nicefact.dao.impl.CommonDAOImpl;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.TaskSubmission;

public class TaskSubmissionDAO extends CommonDAOImpl<TaskSubmission> {
	static Logger logger = Logger.getLogger(TaskSubmissionDAO.class.getSimpleName());

	public TaskSubmissionDAO(EntityManager em) {
		super(em);
	}

	public List<TaskSubmission> getTAskSubmissionsForByTaskId(long taskId) {

		List<TaskSubmission> results = new ArrayList<>();
		EntityManager pm = super.getEntityManager();
		Query query = pm.createQuery("select t from TaskSubmission t where  t.postId = :taskId ");
		query.setParameter("taskId", taskId);
		try {
			results = (List<TaskSubmission>) query.getResultList();
		} catch (NoResultException nre) {
			return new ArrayList<>();
		}
		return results;
	}

	public List<TaskSubmission> getTaskSubmissionsForUserByTaskIds(List<Long> taskIds, String userEmail) {
		
		List<TaskSubmission> results = new ArrayList<>();
		EntityManager pm = super.getEntityManager();
		Query query = pm.createQuery("select t from TaskSubmission t where  t.postId in (:taskIds) and t.createdBy = :email");
		query.setParameter("taskIds", taskIds);
		query.setParameter("email", userEmail);
		try {
			results = (List<TaskSubmission>) query.getResultList();
		} catch (NoResultException nre) {
			return new ArrayList<>();
		}
		return results;
	}
}
