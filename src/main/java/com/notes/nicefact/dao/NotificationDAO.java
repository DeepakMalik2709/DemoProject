package com.notes.nicefact.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.notes.nicefact.dao.impl.CommonDAOImpl;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.Notification;
import com.notes.nicefact.entity.NotificationRecipient;
import com.notes.nicefact.enums.NotificationAction;
import com.notes.nicefact.to.SearchTO;

public class NotificationDAO extends CommonDAOImpl<Notification> {
	static Logger logger = Logger.getLogger(NotificationDAO.class.getSimpleName());
		
		public NotificationDAO(EntityManager em) {
			super(em);
		}
		
		public NotificationRecipient findPostNotificationRecipientByAction(Long entityId, String email, NotificationAction action) {
			EntityManager pm = super.getEntityManager();
			Query query = pm.createQuery("select t from NotificationRecipient t where  t.notification.sender = :email and t.notification.entityId = :entityId  and t.action = :action");
			query.setParameter("action", action);
			query.setParameter("email", email);
			query.setParameter("entityId", entityId);
			try {
				return (NotificationRecipient) query.getSingleResult();
			} catch (NoResultException nre) {
				logger.error(nre.getMessage());
			}
			return null;
		}

		public List<NotificationRecipient> fetchMyNotifications(AppUser appUser, SearchTO searchTO) {
			EntityManager pm = super.getEntityManager();
			Query query = pm.createQuery("select t from NotificationRecipient t where  t.email = :email order by t.createdTime desc");
			query.setParameter("email", appUser.getEmail());
			query.setFirstResult(searchTO.getFirst());
			query.setMaxResults(searchTO.getLimit());
			try {
				return  query.getResultList();
			} catch (NoResultException nre) {
				logger.error(nre.getMessage());
			}
			return new ArrayList<>();
		}

		public List<NotificationRecipient> getAllUnreadRecipientsByEmail(String email) {
			EntityManager pm = super.getEntityManager();
			Query query = pm.createQuery("select t from NotificationRecipient t where  t.email = :email and t.isRead = false order by t.createdTime asc");
			query.setParameter("email", email);
			query.setMaxResults(500);
			try {
				return  query.getResultList();
			} catch (NoResultException nre) {
				logger.error(nre.getMessage());
			}
			return new ArrayList<>();
		}
}
