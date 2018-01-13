package com.notes.nicefact.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.notes.nicefact.dao.impl.CommonDAOImpl;
import com.notes.nicefact.entity.AbstractFile.UPLOAD_TYPE;
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

	public List<PostFile> getAllFilesFromGroup(Long groupId) {
		EntityManager pm = super.getEntityManager();
		Query query = pm.createQuery("select h from PostFile h where h.uploadType = :uploadType and h.post.id in (select a.id from Post a where a.groupId = :groupId)");
		query.setParameter("groupId", groupId);
		query.setParameter("uploadType", UPLOAD_TYPE.GOOGLE_DRIVE);
		query.setMaxResults(1);
		List<PostFile> files = null;
		try {
			files = query.getResultList();
		} catch (NoResultException nre) {
			logger.error(nre.getMessage());
			files = new ArrayList<>();
		}
		return files;
	}
}
