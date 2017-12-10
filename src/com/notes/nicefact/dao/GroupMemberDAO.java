package com.notes.nicefact.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.google.api.services.calendar.model.EventAttendee;
import com.notes.nicefact.dao.impl.CommonDAOImpl;
import com.notes.nicefact.entity.Group;
import com.notes.nicefact.entity.GroupMember;
import com.notes.nicefact.entity.InstituteMember;
import com.notes.nicefact.enums.UserPosition;
import com.notes.nicefact.to.SearchTO;

public class GroupMemberDAO extends CommonDAOImpl<GroupMember> {
	static Logger logger = Logger.getLogger(GroupMemberDAO.class.getSimpleName());

	public GroupMemberDAO(EntityManager em) {
		super(em);
	}
	

/*	public List<GroupMember> fetchGroupMembersByGroupId(long groupId, SearchTO searchTO) {
		List<GroupMember> results = new ArrayList<>();
		EntityManager pm = super.getEntityManager();
		Query query = pm.createQuery("select t from GroupMember t where  t.group.id = :groupId order by t.name");
		query.setParameter("groupId", groupId);
		query.setFirstResult(searchTO.getFirst());
		query.setMaxResults(searchTO.getLimit());
		try {
			results = (List<GroupMember>) query.getResultList();
		} catch (NoResultException nre) {
			logger.warn(nre.getMessage());
		}
		return results;
	}*/
	
	public List<GroupMember> fetchGroupAttendanceMembers( SearchTO searchTO) {
		List<GroupMember> results = new ArrayList<>();
		EntityManager pm = super.getEntityManager();
		Query query = pm.createQuery("select t from GroupMember t where t.isBlocked = false and :position MEMBER OF t.positions and   t.group.id = :groupId and t.isJoinRequestApproved = true order by t.name");
		query.setParameter("groupId", searchTO.getGroupId());
		query.setParameter("position", UserPosition.STUDENT.toString());
		try {
			results = (List<GroupMember>) query.getResultList();
		} catch (NoResultException nre) {
			logger.warn(nre.getMessage());
		}
		return results;
	}
	
	public GroupMember fetchGroupMemberByEmail(long groupId, String email) {
		EntityManager pm = super.getEntityManager();
		Query query = pm.createQuery("select t from GroupMember t where  t.group.id = :groupId and t.email = :email");
		query.setParameter("groupId", groupId);
		query.setParameter("email", email);
		query.setMaxResults(1);
		try {
			GroupMember result = (GroupMember) query.getSingleResult();
			return result;
		} catch (NoResultException nre) {
			logger.warn(nre.getMessage());
		}
		return null;
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


	public List<EventAttendee> getMemberEmailFromGroup(List<Group> groups, SearchTO searchTO) {
		List<EventAttendee> attendees = new ArrayList<EventAttendee>();
		List<GroupMember> results= new ArrayList<GroupMember>();		
		try {
			for (Group grp : groups) {
				results.addAll(fetchByGroupId(grp.getId(),true,searchTO));
			}		
			for (GroupMember groupMember : results) {
				EventAttendee att = new EventAttendee();
				att.setId(groupMember.getId()+"");
				att.setEmail(groupMember.getEmail());				
			}		
		} catch (NoResultException nre) {
			return  new ArrayList<>();
		}
		return attendees;
	}
	
	public List<GroupMember> fetchByGroupId(long groupId, boolean isJoined, SearchTO searchTO) {
		List<GroupMember> results = new ArrayList<>();
		EntityManager pm = super.getEntityManager();
		Query query = pm.createQuery("select t from GroupMember t where t.group.id = :groupId and t.isJoinRequestApproved = :isJoined order by t.email");
		query.setParameter("groupId", groupId);
		query.setParameter("isJoined", isJoined);
		query.setFirstResult(searchTO.getFirst());
		query.setMaxResults(searchTO.getLimit());
		try {
			results = (List<GroupMember>) query.getResultList();
		} catch (NoResultException nre) {
			logger.warn(nre.getMessage());
		}
		return results;
	}
}
