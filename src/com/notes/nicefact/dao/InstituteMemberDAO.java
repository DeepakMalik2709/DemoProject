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
	

	public List<InstituteMember> fetchByInstituteId(long instituteId, SearchTO searchTO) {
		List<InstituteMember> results = new ArrayList<>();
		EntityManager pm = super.getEntityManager();
		Query query = pm.createQuery("select t from InstituteMember t where  t.institute.id = :instituteId order by t.email");
		query.setParameter("groupId", instituteId);
		query.setFirstResult(searchTO.getFirst());
		query.setMaxResults(searchTO.getLimit());
		try {
			results = (List<InstituteMember>) query.getResultList();
		} catch (NoResultException nre) {
			logger.warn(nre.getMessage());
		}
		return results;
	}
	
	public InstituteMember fetchMemberByEmail(long groupId, String email) {
		EntityManager pm = super.getEntityManager();
		Query query = pm.createQuery("select t from InstituteMember t where  t.group.id = :groupId and t.email = :email");
		query.setParameter("groupId", groupId);
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
		Query query = pm.createQuery("select t.group.id from InstituteMember t where  t.email = :email ");
		query.setParameter("email", email);
		try {
			results = (List<Long>) query.getResultList();
		} catch (NoResultException nre) {
			return  new ArrayList<>();
		}
		return results;
	}


}
