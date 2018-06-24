package com.notes.nicefact.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.notes.nicefact.dao.impl.CommonDAOImpl;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.to.SearchInfoTO;

public class AppUserDAO extends CommonDAOImpl<AppUser> {
	static Logger logger = Logger.getLogger(AppUserDAO.class.getSimpleName());

	public AppUserDAO(EntityManager em) {
		super(em);
	}

	public AppUser getByEmail(String email) {
		EntityManager pm = super.getEntityManager();
		Query query = pm.createQuery("select h from AppUser h where h.email = :userMail and isActive = true and isDeleted = false");
		query.setParameter("userMail", email);
		query.setMaxResults(1);
		AppUser obj = null;
		for (int i = 0; i < 3; i++) {
			try {
				obj = (AppUser) query.getSingleResult();
				pm.detach(obj);
				break;
			} catch (NoResultException nre) {
				logger.warn("Error : " + nre.getMessage());
				break;
			} catch (PersistenceException pe) {
				logger.error("Error : " + pe.getMessage(), pe);
			}
		}
		return obj;
	}

	public List<AppUser> getAllUsersByDomain(String domain) {
		List<AppUser> results = null;
		EntityManager pm = super.getEntityManager();
		Query query = pm.createQuery("select h from AppUser h where h.domain = :domainName");
		query.setParameter("domainName", domain);
		AppUser obj = null;
		try {
			results = (List<AppUser>) query.getResultList();
			if (!results.isEmpty()) {
				for (AppUser appUser : results) {
					pm.detach(appUser);
				}
			}
		} catch (NoResultException nre) {
			results = new ArrayList<>();
		} finally {
		}
		return results;
	}

	public AppUser getAppUserByPasswordResetCode(String code) {
		EntityManager pm = super.getEntityManager();
		Query query = pm.createQuery("select h from AppUser h where h.passwordResetCode = :code and isActive = true and isDeleted = false");
		query.setParameter("code", code);
		query.setMaxResults(1);
		AppUser obj = null;
		try {
			obj = (AppUser) query.getSingleResult();
			pm.detach(obj);
		} catch (NoResultException nre) {
		} finally {
		}
		return obj;
	}

	public List<AppUser> search(String term) {
		Set<AppUser> results = new HashSet<>();
		EntityManager pm = super.getEntityManager();
		Query query;
		try {
			try {
				query = pm.createQuery("select h from AppUser h where h.nameForSearch >= :start and h.nameForSearch <= :end and isDeleted = false");
				query.setParameter("start", term);
				query.setParameter("end", term + "\uFFFD");
				results.addAll(query.getResultList());
			} catch (NoResultException nre) {
				logger.info("No result for name : " + term);
			}

			try {
				query = pm.createQuery("select h from AppUser h where h.email >= :start and h.email <= :end and isDeleted = false");
				query.setParameter("start", term);
				query.setParameter("end", term + "\uFFFD");
				results.addAll(query.getResultList());
			} catch (NoResultException nre) {
				logger.info("No result for email : " + term);
			}

			try {
				query = pm.createQuery("select h from AppUser h where h.phoneNumber >= :start and h.phoneNumber <= :end and isDeleted = false");
				query.setParameter("start", term);
				query.setParameter("end", term + "\uFFFD");
				results.addAll(query.getResultList());
			} catch (NoResultException nre) {
				logger.info("No result for phone : " + term);
			}

		} finally {
			for (AppUser appUser : results) {
				pm.detach(appUser);
			}
		}

		return new ArrayList<>(results);
	}
	
	public List<AppUser> getAllUserExceptLoggedIn(SearchInfoTO searchInfoTO, Long appUserId) {
		List<AppUser> results = new ArrayList<>();
		EntityManager pm = super.getEntityManager();
		Query query = pm.createQuery("select au from AppUser au where au.id != :appUserId and where au.name LIKE CONCAT('%',:searchTerm,'%') order by t.updatedTime desc");
		query.setParameter("appUserId", appUserId);
		query.setParameter("searchTerm", searchInfoTO.getSearchData());
		
		query.setFirstResult(searchInfoTO.getFirst());
		query.setMaxResults(searchInfoTO.getLimit());
		
		try {
			results = (List<AppUser>) query.getResultList();
		} catch (NoResultException nre) {
		}
		return results;
	}

}
