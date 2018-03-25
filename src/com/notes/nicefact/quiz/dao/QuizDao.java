/**
 * 
 */
package com.notes.nicefact.quiz.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.notes.nicefact.dao.GroupDAO;
import com.notes.nicefact.dao.impl.CommonDAOImpl;
import com.notes.nicefact.entity.GroupAttendance;
import com.notes.nicefact.quiz.entity.Quiz;
import com.notes.nicefact.to.SearchTO;

/**
 * @author user
 *
 */
public class QuizDao extends CommonDAOImpl<Quiz> {
	
	public QuizDao(EntityManager em) {
		super(em);
		// TODO Auto-generated constructor stub
	}


	static Logger logger = Logger.getLogger(GroupDAO.class.getSimpleName());


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
	
	
	public List<GroupAttendance> getGroupAttendanceByDateRange(SearchTO searchTO, Date fromDate, Date toDate) {
		List<GroupAttendance> results = new ArrayList<>();
		
			EntityManager pm = super.getEntityManager();
			Query query = pm.createQuery("select t from GroupAttendance t where date >= :fromDate and date<= :toDate and  t.groupId = :groupId");
			query.setParameter("groupId",searchTO.getGroupId());
			query.setParameter("fromDate",fromDate);
			query.setParameter("toDate",toDate);
			try {
				results = (List<GroupAttendance>) query.getResultList();
			} catch (NoResultException nre) {
				return new ArrayList<>();
			} 
	
		return results;
	}

}
