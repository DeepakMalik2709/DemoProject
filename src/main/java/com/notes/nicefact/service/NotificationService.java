package com.notes.nicefact.service;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import com.notes.nicefact.dao.CommonDAO;
import com.notes.nicefact.dao.CommonEntityDAO;
import com.notes.nicefact.dao.NotificationDAO;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.Notification;
import com.notes.nicefact.entity.NotificationRecipient;
import com.notes.nicefact.entity.Post;
import com.notes.nicefact.entity.PostComment;
import com.notes.nicefact.enums.NotificationAction;
import com.notes.nicefact.to.NotificationTO;
import com.notes.nicefact.to.SearchTO;
import com.notes.nicefact.util.CacheUtils;

public class NotificationService extends CommonService<Notification> {
	static Logger logger = Logger.getLogger(NotificationService.class.getSimpleName());
		
		private NotificationDAO notificatoinDAO;
		private CommonEntityDAO commonEntityDAO ; 

		public NotificationService(EntityManager em) {
			notificatoinDAO = new NotificationDAO(em);
			commonEntityDAO = new CommonEntityDAO(em);
		}
		

		@Override
		protected CommonDAO<Notification> getDAO() {
			return notificatoinDAO;
		}


		public void upsertRecipient(NotificationRecipient notificationRecipient) {
			commonEntityDAO.upsert(notificationRecipient);
			
		}
		

		public void deletePostReactionNotification(Post post,  String email , NotificationAction action) {
			NotificationRecipient notificationRecipient = notificatoinDAO.findPostNotificationRecipientByAction(post.getId(), email,action );
			if(null !=notificationRecipient){
				remove(notificationRecipient.getNotification());
			}
		}

		public void savePostReactionNotification(Post post,  AppUser user) {
			if(!post.getCreatedBy().equals(user.getEmail())){
				AppUser recipient = CacheUtils.getAppUser(post.getCreatedBy());
				if (null !=recipient ) {
					Notification notification = new Notification(post, user);
					upsert(notification);
					NotificationRecipient notificationRecipient = new NotificationRecipient(recipient);
					notificationRecipient.setSendEmail(recipient.getSendPostLikeEmail());
					notificationRecipient.setAction(NotificationAction.POST_LIKE);
					notificationRecipient.setNotification(notification);
					notification.getRecipients().add(notificationRecipient);
					upsertRecipient(notificationRecipient);
					upsert(notification);
				}
			}
		}
		
		public void saveCommentReactionNotification(Post post, PostComment comment,  AppUser user) {
			if(!comment.getCreatedBy().equals(user.getEmail())){
				AppUser recipient = CacheUtils.getAppUser(comment.getCreatedBy());
				if (null !=recipient ) {
					Notification notification = new Notification(post , comment, user);
					upsert(notification);
					NotificationRecipient notificationRecipient = new NotificationRecipient(recipient);
					notificationRecipient.setSendEmail(recipient.getSendGroupPostMentionEmail());
					notificationRecipient.setAction(NotificationAction.POST_COMMENT_LIKE);
					notificationRecipient.setNotification(notification);
					notification.getRecipients().add(notificationRecipient);
					upsertRecipient(notificationRecipient);
					upsert(notification);
				}
			}
		}


		public List<NotificationTO> fetchMyNotifications(AppUser appUser, SearchTO searchTO) {
			List<NotificationTO> tos = new ArrayList<>();
			NotificationTO to ;
			List<NotificationRecipient> notificationRecipients = notificatoinDAO.fetchMyNotifications(appUser, searchTO);
			for (NotificationRecipient notificationRecipient : notificationRecipients) {
				if (null != notificationRecipient.getNotification()) {
					to = new NotificationTO(notificationRecipient);
					tos.add(to);
				}
			}
			return tos;
		}


		public List<NotificationRecipient> getAllUnreadRecipientsByEmail(String email) {
			return notificatoinDAO.getAllUnreadRecipientsByEmail(email);
		}
}