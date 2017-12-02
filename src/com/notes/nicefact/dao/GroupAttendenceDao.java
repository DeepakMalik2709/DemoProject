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
import com.notes.nicefact.entity.GroupAttendance;
import com.notes.nicefact.to.SearchTO;

/**
 * @author user
 *
 */
public class GroupAttendenceDao extends CommonDAOImpl<GroupAttendance> {
	static Logger logger = Logger.getLogger(GroupDAO.class.getSimpleName());

	public GroupAttendenceDao(EntityManager em) {
		super(em);
	}

	public List<GroupAttendance> fetchMyGroupAttendence(SearchTO searchTO, AppUser appuser) {
		List<GroupAttendance> items = null;
		List<GroupAttendance> results = new ArrayList<>();
		if (!appuser.getGroupIds().isEmpty()) {
			EntityManager pm = super.getEntityManager();
			Query query = pm.createQuery("select t from GroupAttendence t where t.group.id in (:ids) order by t.attendenceDate asc");
			query.setParameter("ids", appuser.getGroupIds());
			query.setFirstResult(searchTO.getFirst());
			query.setMaxResults(searchTO.getLimit());
			try {
				items = (List<GroupAttendance>) query.getResultList();
				if (!items.isEmpty()) {
					for (GroupAttendance tutorial : items) {
						pm.detach(tutorial);
						results.add(tutorial);
					}
				}
			} catch (NoResultException nre) {
			} 
		}
		return results;
	}


	public List<GroupAttendance> fetchGroupsbyIds(Collection<Long> groupAttendenceIds) {
		List<GroupAttendance> results = new ArrayList<>();
		if (null !=groupAttendenceIds && !groupAttendenceIds.isEmpty()) {
			EntityManager pm = super.getEntityManager();
			Query query = pm.createQuery("select t from GroupAttendence t where  t.id in (:groupAttendenceIds) order by t.attendenceDate");
			query.setParameter("groupAttendenceIds", groupAttendenceIds);
			try {
				results = (List<GroupAttendance>) query.getResultList();
			} catch (NoResultException nre) {
				return new ArrayList<>();
			} 
		}
		return results;
	}

	public List<GroupAttendance> getByGroup(Group group) {
		
			List<GroupAttendance> results = new ArrayList<>();
			if (null !=group) {
				EntityManager pm = super.getEntityManager();
				Query query = pm.createQuery("select t from GroupAttendence t where  t.group.id in (:groupIds) order by t.attendenceDate");
				query.setParameter("groupIds", group.getId());
				try {
					results = (List<GroupAttendance>) query.getResultList();
				} catch (NoResultException nre) {
					return new ArrayList<>();
				} 
			}
			return results;
		}
	

}
