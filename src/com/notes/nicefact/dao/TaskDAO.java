package com.notes.nicefact.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.notes.nicefact.dao.impl.CommonDAOImpl;
import com.notes.nicefact.entity.Task;
import com.notes.nicefact.to.SearchTO;

public class TaskDAO  extends CommonDAOImpl<Task> {
	static Logger logger = Logger.getLogger(TaskDAO.class.getSimpleName());

	public TaskDAO(EntityManager em) {
		super(em);
	}
	
	
	public List<Task> search(SearchTO searchTO) {
		List<Task> results = new ArrayList<>();
		EntityManager pm = super.getEntityManager();
		Query query = pm.createQuery("select t from Task t where  t.groupId = :groupId order by t.updatedTime desc");
		query.setParameter("groupId", searchTO.getGroupId());
		query.setFirstResult(searchTO.getFirst());

		query.setMaxResults(searchTO.getLimit());
		try {
			results = (List<Task>) query.getResultList();
		} catch (NoResultException nre) {
		}
		return results;
	}
}
