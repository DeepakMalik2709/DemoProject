package com.notes.nicefact.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.notes.nicefact.dao.impl.CommonDAOImpl;
import com.notes.nicefact.entity.InstituteMember;
import com.notes.nicefact.to.SearchTO;

public class InstituteMemberDAO extends CommonDAOImpl<InstituteMember> {
	static Logger logger = Logger.getLogger(InstituteMemberDAO.class.getSimpleName());

	public InstituteMemberDAO(EntityManager em) {
		super(em);
	}
	

	public List<InstituteMember> fetchByInstituteId(long instituteId, boolean isJoined, SearchTO searchTO) {
		List<InstituteMember> results = new ArrayList<>();
		EntityManager pm = super.getEntityManager();
		Query query = pm.createQuery("select t from InstituteMember t where  t.institute.id = :instituteId and t.isJoinRequestApproved = :isJoined order by t.email");
		query.setParameter("instituteId", instituteId);
		query.setParameter("isJoined", isJoined);
		query.setFirstResult(searchTO.getFirst());
		query.setMaxResults(searchTO.getLimit());
		try {
			results = (List<InstituteMember>) query.getResultList();
		} catch (NoResultException nre) {
			logger.warn(nre.getMessage());
		}
		return results;
	}
	
	public InstituteMember fetchMemberByEmail(long instituteId, String email) {
		EntityManager pm = super.getEntityManager();
		Query query = pm.createQuery("select t from InstituteMember t where  t.institute.id = :instituteId and t.email = :email");
		query.setParameter("instituteId", instituteId);
		query.setParameter("email", email);
		query.setMaxResults(1);
		try {
			InstituteMember result = (InstituteMember) query.getSingleResult();
			return result;
		} catch (NoResultException nre) {
			logger.warn(nre.getMessage());
		}
		return null;
	}
	
	public List<Long> fetchGroupMembersByEmail(String email) {
		List<Long> results = new ArrayList<>();
		EntityManager pm = super.getEntityManager();
		Query query = pm.createQuery("select t.institute.id from InstituteMember t where  t.email = :email ");
		query.setParameter("email", email);
		try {
			results = (List<Long>) query.getResultList();
		} catch (NoResultException nre) {
			return  new ArrayList<>();
		}
		return results;
	}


	public List<InstituteMember> fetchJoinedInstituteMembers(String email) {
		List<InstituteMember> results = new ArrayList<>();
		EntityManager pm = super.getEntityManager();
		Query query = pm.createQuery("select t from InstituteMember t where  t.email = :email");
		query.setParameter("email", email);
		try {
			results = (List<InstituteMember>) query.getResultList();
		} catch (NoResultException nre) {
			logger.warn(nre.getMessage());
		}
		return results;
	}

}
