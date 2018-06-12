package com.notes.nicefact.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.notes.nicefact.dao.impl.CommonDAOImpl;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.Institute;
import com.notes.nicefact.to.SearchTO;

public class InstituteDAO extends CommonDAOImpl<Institute> {
	static Logger logger = Logger.getLogger(InstituteDAO.class.getSimpleName());

	public InstituteDAO(EntityManager em) {
		super(em);
	}

	public List<Institute> fetchMyInstitutes(SearchTO searchTO, AppUser appuser) {
		List<Institute> items = null;
		List<Institute> results = new ArrayList<>();
		if (!appuser.getInstituteIds().isEmpty()) {
			EntityManager pm = super.getEntityManager();
			Query query = pm.createQuery("select t from Institute t where t.id in (:ids) order by t.name asc");
			query.setParameter("ids", appuser.getInstituteIds());
			query.setFirstResult(searchTO.getFirst());
			query.setMaxResults(searchTO.getLimit());
			try {
				items = (List<Institute>) query.getResultList();
				if (!items.isEmpty()) {
					for (Institute tutorial : items) {
						pm.detach(tutorial);
						results.add(tutorial);
					}
				}
			} catch (NoResultException nre) {
			} 
		}
		return results;
	}


	public List<Institute> fetchInstitutesbyIds(Collection<Long> groupIds) {
		List<Institute> results = new ArrayList<>();
		if (null !=groupIds && !groupIds.isEmpty()) {
			EntityManager pm = super.getEntityManager();
			Query query = pm.createQuery("select t from Institute t where  t.id in (:groupIds) order by t.name");
			query.setParameter("groupIds", groupIds);
			try {
				results = (List<Institute>) query.getResultList();
			} catch (NoResultException nre) {
				return new ArrayList<>();
			} 
		}
		return results;
	}


	public List<Institute> search(SearchTO searchTO) {
		List<Institute> results = new ArrayList<>();
		if (StringUtils.isNotBlank(searchTO.getSearchTerm())) {
			EntityManager pm = super.getEntityManager();
			Query query = pm.createQuery("select t from Institute t where t.name like :term order by t.name");
			query.setParameter("term", "%" + searchTO.getSearchTerm() + "%");
			query.setFirstResult(searchTO.getFirst());
			query.setMaxResults(searchTO.getLimit());
			
			try {
				results = (List<Institute>) query.getResultList();
			} catch (NoResultException nre) {
				return new ArrayList<>();
			} 
		}
		return results;
	}

}
