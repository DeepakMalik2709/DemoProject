package com.notes.nicefact.dao;

import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.notes.nicefact.dao.impl.CommonDAOImpl;
import com.notes.nicefact.entity.GroupMember;
import com.notes.nicefact.to.SearchTO;

public class GroupMemberDAO extends CommonDAOImpl<GroupMember> {
	static Logger logger = Logger.getLogger(GroupMemberDAO.class.getSimpleName());

	public GroupMemberDAO(EntityManager em) {
		super(em);
	}
	

	public List<GroupMember> fetchGroupMembersByGroupId(long groupId, SearchTO searchTO) {
		List<GroupMember> results = new ArrayList<>();
		EntityManager pm = super.getEntityManager();
		Query query = pm.createQuery("select t from GroupMember t where  t.group.id = :groupId order by t.email");
		query.setParameter("groupId", groupId);
		query.setFirstResult(searchTO.getFirst());
		query.setMaxResults(searchTO.getLimit());
		try {
			results = (List<GroupMember>) query.getResultList();
		} catch (NoResultException nre) {
		}
		return results;
	}
	
	public List<Long> fetchGroupMembersByEmail(String email) {
		List<Long> results = new ArrayList<>();
		EntityManager pm = super.getEntityManager();
		Query query = pm.createQuery("select t.group.id from GroupMember t where  t.email = :email ");
		query.setParameter("email", email);
		try {
			results = (List<Long>) query.getResultList();
		} catch (NoResultException nre) {
			return  new ArrayList<>();
		}
		return results;
	}
}
