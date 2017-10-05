package com.notes.nicefact.dao;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.notes.nicefact.dao.impl.CommonDAOImpl;
import com.notes.nicefact.entity.PostFile;

public class PostFileDAO extends CommonDAOImpl<PostFile> {
	static Logger logger = Logger.getLogger(PostFileDAO.class);

	public PostFileDAO(EntityManager em) {
		super(em);
	}

	public PostFile getByServerName(String serverName) {
		EntityManager pm = super.getEntityManager();
		Query query = pm.createQuery("select h from PostFile h where h.serverName = :serverName");
		query.setParameter("serverName", serverName);
		query.setMaxResults(1);
		PostFile obj = null;
		try {
			obj = (PostFile) query.getSingleResult();
		} catch (NoResultException nre) {
			logger.error(nre.getMessage());
		}
		return obj;
	}
}
