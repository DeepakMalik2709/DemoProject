package com.notes.nicefact.dao.profile;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.notes.nicefact.dao.impl.CommonDAOImpl;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.Certificate;

public class CertificateDao extends CommonDAOImpl<Certificate> {

	private static Logger logger = Logger.getLogger(CertificateDao.class.getSimpleName());
	
	public CertificateDao(EntityManager em) {
		super(em);
	}

	public List<Certificate> getByAppUserId(Long id) {
		List<Certificate> results = null;
		EntityManager pm = super.getEntityManager();
		Query query = pm.createQuery("select c from Certificate c where c.appUser.id = :appUserId");
		query.setParameter("appUserId", id);
		try {
			results = (List<Certificate>) query.getResultList();
			if (!results.isEmpty()) {
				for (Certificate certificate : results) {
					pm.detach(certificate);
				}
			}
		} catch (NoResultException nre) {
			results = new ArrayList<>();
		} finally {
		}
		
		return results;
	}
}
