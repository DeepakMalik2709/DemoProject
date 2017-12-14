/**
 * 
 */
package com.notes.nicefact.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.notes.nicefact.dao.impl.CommonDAOImpl;
import com.notes.nicefact.entity.GroupAttendance;
import com.notes.nicefact.entity.StudentAttendance;
import com.notes.nicefact.to.SearchTO;

/**
 * @author user
 *
 */
public class StudentAttendenceDao extends CommonDAOImpl<StudentAttendance> {
	static Logger logger = Logger.getLogger(GroupDAO.class.getSimpleName());

	public StudentAttendenceDao(EntityManager em) {
		super(em);
	}


	public List<StudentAttendance> fetchGroupsbyIds(Collection<Long> groupAttendenceIds) {
		List<StudentAttendance> results = new ArrayList<>();
		if (null !=groupAttendenceIds && !groupAttendenceIds.isEmpty()) {
			EntityManager pm = super.getEntityManager();
			Query query = pm.createQuery("select t from StudentAttendance t where  t.groupAttendence.id in (:groupAttendenceIds) order by t.name");
			query.setParameter("groupAttendenceIds", groupAttendenceIds);
			try {
				results = (List<StudentAttendance>) query.getResultList();
			} catch (NoResultException nre) {
				return new ArrayList<>();
			} 
		}
		return results;
	}

	public List<StudentAttendance> getByGroupAttendence(GroupAttendance groupAttendence) {
		
			List<StudentAttendance> results = new ArrayList<>();
			if (null !=groupAttendence) {
				EntityManager pm = super.getEntityManager();
				Query query = pm.createQuery("select t from StudentAttendance t where  t.groupAttendence.id in (:groupIds) order by t.name");
				query.setParameter("groupIds", groupAttendence.getId());
				try {
					results = (List<StudentAttendance>) query.getResultList();
				} catch (NoResultException nre) {
					return new ArrayList<>();
				} 
			}
			return results;
		}


	public List<StudentAttendance> fetchStudentAttendance(SearchTO searchTO,long groupId, String email, Date fromDate, Date toDate) {
		List<StudentAttendance> results = new ArrayList<>();
		
			EntityManager pm = super.getEntityManager();
			Query query = pm.createQuery("select t from StudentAttendance t where date >= :fromDate and date<= :toDate and  t.groupId = :groupId and t.email= :email order by t.name");
			query.setParameter("groupId",groupId);
			query.setParameter("email",email);
			query.setParameter("fromDate",fromDate);
			query.setParameter("toDate",toDate);
			try {
				results = (List<StudentAttendance>) query.getResultList();
			} catch (NoResultException nre) {
				return new ArrayList<>();
			} 
	
		return results;
	}
	

}
