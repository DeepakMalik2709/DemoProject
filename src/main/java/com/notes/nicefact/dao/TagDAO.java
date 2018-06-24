package com.notes.nicefact.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.notes.nicefact.dao.impl.CommonDAOImpl;
import com.notes.nicefact.entity.Tag;
import com.notes.nicefact.to.SearchInfoTO;
import com.notes.nicefact.to.SearchTO;

public class TagDAO extends CommonDAOImpl<Tag>{
	static Logger logger = Logger.getLogger(TagDAO.class.getSimpleName());
	
	public TagDAO(EntityManager em) {
		super(em);
	}

	public List<Tag> search(SearchTO searchTO) {
		List<Tag> items = null;
		List<Tag> results = new ArrayList<>();
		EntityManager pm = super.getEntityManager();
		Query query = pm.createQuery("select t from Tag t where t.name LIKE CONCAT('%',:searchTerm,'%') order by t.name asc");
		query.setParameter("searchTerm", searchTO.getSearchTerm());
		query.setFirstResult(searchTO.getFirst());
		query.setMaxResults(searchTO.getLimit());
		try {
			items = (List<Tag>) query.getResultList();
			if (!items.isEmpty()) {
				for (Tag tutorial : items) {
					pm.detach(tutorial);
					results.add(tutorial);
				}
			}
		} catch (NoResultException nre) {
		}
		return results;
	}
	
	public List<Tag> search(SearchInfoTO searchInfoTO) {
		List<Tag> items = null;
		List<Tag> results = new ArrayList<>();
		EntityManager pm = super.getEntityManager();
		Query query = pm.createQuery("select t from Tag t where t.name LIKE CONCAT('%',:searchTerm,'%') order by t.name asc");
		query.setParameter("searchTerm", searchInfoTO.getSearchData());
		query.setFirstResult(searchInfoTO.getFirst());
		query.setMaxResults(searchInfoTO.getLimit());
		try {
			items = (List<Tag>) query.getResultList();
			if (!items.isEmpty()) {
				for (Tag tutorial : items) {
					pm.detach(tutorial);
					results.add(tutorial);
				}
			}
		} catch (NoResultException nre) {
		}
		return results;
	}
	
	public Tag getByName(String name) {
		Tag tag = null;
		
		EntityManager pm = super.getEntityManager();
		Query query = pm.createQuery("select t from Tag t where t.name=:name order by t.name asc");
		query.setParameter("name", name);
		try {
			tag = (Tag) query.getSingleResult();
		} catch (NoResultException nre) {
		}
		
		return tag;
	}
}
