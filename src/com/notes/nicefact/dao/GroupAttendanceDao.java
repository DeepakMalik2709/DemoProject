/**
 * 
 */
package com.notes.nicefact.dao;

import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.notes.nicefact.dao.impl.CommonDAOImpl;
import com.notes.nicefact.entity.GroupAttendance;
import com.notes.nicefact.to.SearchTO;

/**
 * @author user
 *
 */
public class GroupAttendanceDao extends CommonDAOImpl<GroupAttendance> {
	static Logger logger = Logger.getLogger(GroupDAO.class.getSimpleName());

	public GroupAttendanceDao(EntityManager em) {
		super(em);
	}


	public GroupAttendance getByGroupDate(SearchTO searchTO) {
		EntityManager pm = super.getEntityManager();
		Query query = pm.createQuery("select t from GroupAttendance t where  t.group.id = :groupId and t.fromTime = :fromTime  and t.date = :date");
		query.setParameter("groupId", searchTO.getGroupId());
		query.setParameter("fromTime",searchTO.getFromTime());
		query.setParameter("date", new Date( searchTO.getDate()));
		try {
			return (GroupAttendance) query.getSingleResult();
		} catch (NoResultException nre) {
		} 
		return null;
	}
	

}
