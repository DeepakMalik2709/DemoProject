package com.notes.nicefact.dao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.notes.nicefact.dao.impl.CommonDAOImpl;
import com.notes.nicefact.entity.AbstractFile.UPLOAD_TYPE;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.Post;
import com.notes.nicefact.entity.PostFile;
import com.notes.nicefact.to.SearchTO;
import com.notes.nicefact.util.Constants;

public class PostDAO extends CommonDAOImpl<Post> {
	static Logger logger = Logger.getLogger(PostDAO.class.getSimpleName());

	public PostDAO(EntityManager em) {
		super(em);
	}

	public List<Post> search(SearchTO searchTO) {
		List<Post> results = new ArrayList<>();
		EntityManager pm = super.getEntityManager();
		Query query = pm.createQuery("select t from Post t where  t.groupId = :groupId order by t.updatedTime desc");
		query.setParameter("groupId", searchTO.getGroupId());
		query.setFirstResult(searchTO.getFirst());

		query.setMaxResults(searchTO.getLimit());
		try {
			results = (List<Post>) query.getResultList();
		} catch (NoResultException nre) {
		}
		return results;
	}

	public List<Post> fetchMyPosts(SearchTO searchTO , AppUser appuser) {
		List<Post> results = new ArrayList<>();
		if (!appuser.getGroupIds().isEmpty()) {
			EntityManager pm = super.getEntityManager();
			Query query = pm.createQuery("select t from Post t where  t.groupId in (:groupIds) order by t.updatedTime desc");
			query.setParameter("groupIds", appuser.getGroupIds());
			query.setFirstResult(searchTO.getFirst());
			query.setMaxResults(searchTO.getLimit());
			try {
				results = (List<Post>) query.getResultList();
			} catch (NoResultException nre) {
			} 
		}
		return results;
	}

	public List<PostFile> getPostFilesWithTempDriveId(int offset) {
		List<PostFile> results = new ArrayList<>();
		EntityManager pm = super.getEntityManager();
		Query query = pm.createQuery("select t from PostFile t where t.createdTime < :createdTime and uploadType=:uploadType and  t.tempGoogleDriveId IS NOT NULL order by t.createdTime asc");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, -20);
		query.setParameter("createdTime", cal.getTime());
		query.setParameter("uploadType", UPLOAD_TYPE.SERVER);
		query.setFirstResult(offset);

		query.setMaxResults(Constants.RECORDS_100);
		try {
			results = (List<PostFile>) query.getResultList();
		} catch (NoResultException nre) {
			logger.error(nre.getMessage());
		}
		return results;
	}

	public List<PostFile> getDrivePostFilesWithoutThumbnail(int offset) {
		List<PostFile> results = new ArrayList<>();
		EntityManager pm = super.getEntityManager();
		Query query = pm.createQuery("select t from PostFile t where t.createdTime > :createdTime and uploadType=:uploadType and  t.thumbnail IS NULL order by t.createdTime asc");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -1);
		query.setParameter("uploadType", UPLOAD_TYPE.GOOGLE_DRIVE);
		query.setParameter("createdTime", cal.getTime());
		query.setFirstResult(offset);

		query.setMaxResults(Constants.RECORDS_100);
		try {
			results = (List<PostFile>) query.getResultList();
		} catch (NoResultException nre) {
			logger.error(nre.getMessage());
		}
		return results;
	}

	public List<Post> fetchScheduleByDate(SearchTO searchTO, Date date) {
		List<Post> results = new ArrayList<>();		
		EntityManager pm = super.getEntityManager();
		Query query = pm.createQuery("select t from Post t where  t.postType='SCHEDULE' and t.isDeleted = :isDeleted and t.isActive = :isActive"+
						" and date(t.fromDate) <= :todayDate and date(t.toDate) >= :todayDate");
		query.setParameter("todayDate",date);
		query.setParameter("isDeleted", false);
		query.setParameter("isActive", true);
		query.setFirstResult(searchTO.getFirst());
		query.setMaxResults(searchTO.getLimit());
		try {
			results = (List<Post>) query.getResultList();
		} catch (NoResultException nre) {
			
		} 		
		return results;
	}

	public int countScheduleByDateAndDay(Date date) {
		int count = 0;
		EntityManager pm = getEntityManager();
		try {
			Query query = pm.createQuery("select count(t) from Post t where  t.postType='SCHEDULE' and t.isDeleted = :isDeleted and t.isActive = :isActive"+
					" and date(t.fromDate) <= :todayDate and date(t.toDate) >= :todayDate ");
			Calendar calendar = Calendar.getInstance();
	        calendar.setTime(date);
	        System.out.println(calendar.get(Calendar.DAY_OF_WEEK));
			query.setParameter("todayDate",calendar.getTime());
			//query.setParameter("weekday",WeekDay.getByNumber(calendar.get(Calendar.DAY_OF_WEEK)).toString());
			query.setParameter("isDeleted", false);
			query.setParameter("isActive", true);
			Number result = (Number) query.getSingleResult();
			count = result.intValue();
		} catch (NoResultException nre) {
		} finally {
			
		}
		return count;
	}

}
