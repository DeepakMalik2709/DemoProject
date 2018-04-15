/**
 * 
 */
package com.notes.nicefact.quiz.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.notes.nicefact.dao.GroupDAO;
import com.notes.nicefact.dao.impl.CommonDAOImpl;
import com.notes.nicefact.quiz.entity.Quiz;

/**
 * @author user
 *
 */
public class QuizDao extends CommonDAOImpl<Quiz> {
	static Logger logger = Logger.getLogger(GroupDAO.class.getSimpleName());

	public QuizDao(EntityManager em) {
		super(em);
		// TODO Auto-generated constructor stub
	}

	public List<Quiz> getQuizForGroups(String string, Set<Long> groupIds, int first, int limit, Object object) {
		List<Quiz> results = new ArrayList<>();
		
		EntityManager pm = super.getEntityManager();
		Query query = pm.createQuery("select t from Quiz t join t.groups tg where t.isActive = true and t.isDeleted = false");
//		query.setParameter("groupdIds",groupIds);
		try {
			results = (List<Quiz>) query.getResultList();
		} catch (NoResultException nre) {
			return new ArrayList<>();
		} 
	return results;		
	}

}
