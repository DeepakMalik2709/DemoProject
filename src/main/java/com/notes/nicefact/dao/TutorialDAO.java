package com.notes.nicefact.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.notes.nicefact.dao.impl.CommonDAOImpl;
import com.notes.nicefact.entity.AbstractFile.UPLOAD_TYPE;
import com.notes.nicefact.entity.Tutorial;
import com.notes.nicefact.entity.TutorialFile;
import com.notes.nicefact.to.SearchTO;
import com.notes.nicefact.util.Constants;

public class TutorialDAO extends CommonDAOImpl<Tutorial> {
	static Logger logger = Logger.getLogger(TutorialDAO.class.getSimpleName());

	public TutorialDAO(EntityManager em) {
		super(em);
	}

	public List<Tutorial> search(SearchTO searchTO) {
		List<Tutorial> items = null;
		List<Tutorial> results = new ArrayList<>();
		if (StringUtils.isNotBlank(searchTO.getSearchTerm())) {
			EntityManager pm = super.getEntityManager();
			Query query = pm.createQuery(
					"select t from Tutorial t where  t.description LIKE CONCAT('%',:searchTerm,'%') OR t.title LIKE CONCAT('%',:searchTerm,'%') OR t.id in (select t.id from Tutorial t join t.tagIds  p where p in (select m.id from Tag m where m.name = :searchTerm) ) order by t.updatedTime desc");
			query.setParameter("searchTerm", searchTO.getSearchTerm());
			query.setFirstResult(searchTO.getFirst());
			query.setMaxResults(searchTO.getLimit());
			try {
				items = (List<Tutorial>) query.getResultList();
				if (!items.isEmpty()) {
					for (Tutorial tutorial : items) {
						pm.detach(tutorial);
						results.add(tutorial);
					}
				}
			} catch (NoResultException nre) {
			} 
		}
		return results;
	}
	
	public List<Tutorial> fetchTrendingTutorialList(SearchTO searchTO) {
		List<Tutorial> items = null;
		List<Tutorial> results = new ArrayList<>();
		EntityManager pm = super.getEntityManager();
		Query query = pm.createQuery("select t from Tutorial t order by t.createdTime desc");
		query.setFirstResult(searchTO.getFirst());
		query.setMaxResults(searchTO.getLimit());
		try {
			items = (List<Tutorial>) query.getResultList();
			if (!items.isEmpty()) {
				for (Tutorial tutorial : items) {
					pm.detach(tutorial);
					results.add(tutorial);
				}
			}
		} catch (NoResultException nre) {
		}
		return results;
	}

	public List<Tutorial> fetchMyTutorialList(SearchTO searchTO) {
		List<Tutorial> items = null;
		List<Tutorial> results = new ArrayList<>();
		EntityManager pm = super.getEntityManager();
		Query query = pm.createQuery("select t from Tutorial t where  t.createdBy = :email");
		query.setParameter("email", searchTO.getEmail());
		query.setFirstResult(searchTO.getFirst());
		query.setMaxResults(searchTO.getLimit());
		try {
			items = (List<Tutorial>) query.getResultList();
			if (!items.isEmpty()) {
				for (Tutorial tutorial : items) {
					pm.detach(tutorial);
					results.add(tutorial);
				}
			}
		} catch (NoResultException nre) {
		}
		return results;
	}

	public TutorialFile getByServerName(String serverName) {
		EntityManager pm = super.getEntityManager();
		Query query = pm.createQuery("select h from TutorialFile h where h.serverName = :serverName");
		query.setParameter("serverName", serverName);
		query.setMaxResults(1);
		TutorialFile obj = null;
		try {
			obj = (TutorialFile) query.getSingleResult();
		} catch (NoResultException nre) {
			logger.error(nre.getMessage());
		}
		return obj;
	}
	
	public List<TutorialFile> getTutorialFilesWithTempDriveId(int offset) {
		List<TutorialFile> results = new ArrayList<>();
		EntityManager pm = super.getEntityManager();
		Query query = pm.createQuery("select t from TutorialFile t where t.createdTime < :createdTime and uploadType=:uploadType and  t.tempGoogleDriveId IS NOT NULL order by t.createdTime asc");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -20);
		query.setParameter("createdTime", cal.getTime());
		query.setParameter("uploadType", UPLOAD_TYPE.SERVER);
		query.setFirstResult(offset);

		query.setMaxResults(Constants.RECORDS_100);
		try {
			results = (List<TutorialFile>) query.getResultList();
		} catch (NoResultException nre) {
			logger.error(nre.getMessage());
		}
		return results;
	}

	public List<TutorialFile> getDriveTutorialFilesWithoutThumbnail(int offset) {
		List<TutorialFile> results = new ArrayList<>();
		EntityManager pm = super.getEntityManager();
		Query query = pm.createQuery("select t from TutorialFile t where t.createdTime > :createdTime and uploadType=:uploadType and  t.thumbnail IS NULL order by t.createdTime asc");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -1);
		query.setParameter("uploadType", UPLOAD_TYPE.GOOGLE_DRIVE);
		query.setParameter("createdTime", cal.getTime());
		query.setFirstResult(offset);

		query.setMaxResults(Constants.RECORDS_100);
		try {
			results = (List<TutorialFile>) query.getResultList();
		} catch (NoResultException nre) {
			logger.error(nre.getMessage());
		}
		return results;
	}
	
}
