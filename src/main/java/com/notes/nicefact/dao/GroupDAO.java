package com.notes.nicefact.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.notes.nicefact.dao.impl.CommonDAOImpl;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.Group;
import com.notes.nicefact.to.SearchTO;

public class GroupDAO extends CommonDAOImpl<Group> {
	static Logger logger = Logger.getLogger(GroupDAO.class.getSimpleName());

	public GroupDAO(EntityManager em) {
		super(em);
	}

	public List<Group> fetchMyGroups(SearchTO searchTO, AppUser appuser) {
		List<Group> items = null;
		List<Group> results = new ArrayList<>();
		if (!appuser.getGroupIds().isEmpty()) {
			EntityManager pm = super.getEntityManager();
			Query query = pm.createQuery("select t from Group t where t.id in (:ids) order by t.name asc");
			query.setParameter("ids", appuser.getGroupIds());
			query.setFirstResult(searchTO.getFirst());
			query.setMaxResults(searchTO.getLimit());
			try {
				items = (List<Group>) query.getResultList();
				if (!items.isEmpty()) {
					for (Group tutorial : items) {
						pm.detach(tutorial);
						results.add(tutorial);
					}
				}
			} catch (NoResultException nre) {
			} 
		}
		return results;
	}


	public List<Group> fetchGroupsbyIds(Collection<Long> groupIds) {
		List<Group> results = new ArrayList<>();
		if (null !=groupIds && !groupIds.isEmpty()) {
			EntityManager pm = super.getEntityManager();
			Query query = pm.createQuery("select t from Group t where  t.id in (:groupIds) order by t.name");
			query.setParameter("groupIds", groupIds);
			try {
				results = (List<Group>) query.getResultList();
			} catch (NoResultException nre) {
				return new ArrayList<>();
			} 
		}
		return results;
	}

	public List<Long> getParentGroupsIds(Long id) {
		List<Long> results = new ArrayList<>();
		EntityManager pm = super.getEntityManager();
		Query query = pm.createQuery("select g.id from Group g join g.memberGroupsIds m where m= :groupId");
		query.setParameter("groupId", id);
		try {
			results = (List<Long>) query.getResultList();
		} catch (NoResultException nre) {
			return new ArrayList<>();
		}
		return results;
	}

	public List<Group> fetchGroupChildren(long groupId, SearchTO searchTO) {
		List<Group> results = new ArrayList<>();
		EntityManager pm = super.getEntityManager();
		Group group = get(groupId);
		if (group != null) {
			return fetchGroupsbyIds(group.getMemberGroupsIds());
		}
		return results;
	}

	public List<Group> fetchInstituteChildren(long instituteId, SearchTO searchTO) {
		List<Group> results = new ArrayList<>();
			EntityManager pm = super.getEntityManager();
			Query query = pm.createQuery("select t from Group t where  t.institute.id = :instituteId order by t.name");
			query.setParameter("instituteId", instituteId);
			try {
				results = (List<Group>) query.getResultList();
			} catch (NoResultException nre) {
				return new ArrayList<>();
			} 
		return results;
	}

}
