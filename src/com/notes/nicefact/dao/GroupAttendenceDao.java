/**
 * 
 */
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
import com.notes.nicefact.entity.GroupAttendence;
import com.notes.nicefact.to.SearchTO;

/**
 * @author user
 *
 */
public class GroupAttendenceDao extends CommonDAOImpl<GroupAttendence> {
	static Logger logger = Logger.getLogger(GroupDAO.class.getSimpleName());

	public GroupAttendenceDao(EntityManager em) {
		super(em);
	}

	public List<GroupAttendence> fetchMyGroupAttendence(SearchTO searchTO, AppUser appuser) {
		List<GroupAttendence> items = null;
		List<GroupAttendence> results = new ArrayList<>();
		if (!appuser.getGroupIds().isEmpty()) {
			EntityManager pm = super.getEntityManager();
			Query query = pm.createQuery("select t from GroupAttendence t where t.group.id in (:ids) order by t.attendenceDate asc");
			query.setParameter("ids", appuser.getGroupIds());
			query.setFirstResult(searchTO.getFirst());
			query.setMaxResults(searchTO.getLimit());
			try {
				items = (List<GroupAttendence>) query.getResultList();
				if (!items.isEmpty()) {
					for (GroupAttendence tutorial : items) {
						pm.detach(tutorial);
						results.add(tutorial);
					}
				}
			} catch (NoResultException nre) {
			} 
		}
		return results;
	}


	public List<GroupAttendence> fetchGroupsbyIds(Collection<Long> groupAttendenceIds) {
		List<GroupAttendence> results = new ArrayList<>();
		if (null !=groupAttendenceIds && !groupAttendenceIds.isEmpty()) {
			EntityManager pm = super.getEntityManager();
			Query query = pm.createQuery("select t from GroupAttendence t where  t.id in (:groupAttendenceIds) order by t.attendenceDate");
			query.setParameter("groupAttendenceIds", groupAttendenceIds);
			try {
				results = (List<GroupAttendence>) query.getResultList();
			} catch (NoResultException nre) {
				return new ArrayList<>();
			} 
		}
		return results;
	}

	public List<GroupAttendence> getByGroup(Group group) {
		
			List<GroupAttendence> results = new ArrayList<>();
			if (null !=group) {
				EntityManager pm = super.getEntityManager();
				Query query = pm.createQuery("select t from GroupAttendence t where  t.group.id in (:groupIds) order by t.attendenceDate");
				query.setParameter("groupIds", group.getId());
				try {
					results = (List<GroupAttendence>) query.getResultList();
				} catch (NoResultException nre) {
					return new ArrayList<>();
				} 
			}
			return results;
		}
	

}
