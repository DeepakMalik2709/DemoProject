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
import com.notes.nicefact.entity.GroupAttendance;
import com.notes.nicefact.entity.StudentAttendance;

/**
 * @author user
 *
 */
public class StudentAttendenceDao extends CommonDAOImpl<StudentAttendance> {
	static Logger logger = Logger.getLogger(GroupDAO.class.getSimpleName());

	public StudentAttendenceDao(EntityManager em) {
		super(em);
	}


	public List<StudentAttendance> fetchGroupsbyIds(Collection<Long> groupAttendanceIds) {
		List<StudentAttendance> results = new ArrayList<>();
		if (null !=groupAttendanceIds && !groupAttendanceIds.isEmpty()) {
			EntityManager pm = super.getEntityManager();
			Query query = pm.createQuery("select t from StudentAttendance t where  t.groupAttendance.id in (:groupAttendenceIds) order by t.name");
			query.setParameter("groupAttendenceIds", groupAttendanceIds);
			try {
				results = (List<StudentAttendance>) query.getResultList();
			} catch (NoResultException nre) {
				return new ArrayList<>();
			} 
		}
		return results;
	}

	public List<StudentAttendance> getByGroupAttendence(GroupAttendance groupAttendance) {
		
			List<StudentAttendance> results = new ArrayList<>();
			if (null !=groupAttendance) {
				EntityManager pm = super.getEntityManager();
				Query query = pm.createQuery("select t from StudentAttendance t where  t.groupAttendance.id in (:groupIds) order by t.name");
				query.setParameter("groupIds", groupAttendance.getId());
				try {
					results = (List<StudentAttendance>) query.getResultList();
				} catch (NoResultException nre) {
					return new ArrayList<>();
				} 
			}
			return results;
		}

	public List<StudentAttendance> getAllByGroupId(Long groupId) {
		
		List<StudentAttendance> results = new ArrayList<>();
		if (null !=groupId) {
			EntityManager pm = super.getEntityManager();
			Query query = pm.createQuery("select t from StudentAttendance t where  t.groupId = :groupId order by t.email");
			query.setParameter("groupId", groupId);
			try {
				results = (List<StudentAttendance>) query.getResultList();
			} catch (NoResultException nre) {
				return new ArrayList<>();
			} 
		}
		return results;
	}
	

}
