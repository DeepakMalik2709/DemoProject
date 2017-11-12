package com.notes.nicefact.to;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.notes.nicefact.entity.Notification;
import com.notes.nicefact.entity.NotificationRecipient;
import com.notes.nicefact.enums.NotificationAction;
import com.notes.nicefact.enums.NotificationType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationTO implements Serializable {

	private static final long serialVersionUID = 1L;

	public NotificationTO() {
	}

	public NotificationTO(NotificationRecipient notificationRecipient) {
		Notification notification = notificationRecipient.getNotification();
		this.id = notification.getId();
		this.subEntityId = notification.getSubEntityId();
		this.entityId = notification.getEntityId();
		this.createdTime = notification.getCreatedTime().getTime();
		if(StringUtils.isBlank(notification.getTitle())){
			this.title = "a post";
		}else{
			this.title = notification.getTitle();
		}
		this.sender = notification.getSender();
		this.senderPosition = notification.getSenderPosition();
		this.senderName = notification.getSenderName();
		this.groupId = notification.getGroupId();
		this.groupName = notification.getGroupName();
		this.action = notificationRecipient.getAction().toString();
		this.actionKey = notificationRecipient.getAction().getMessageKey();
		this.isRead = notificationRecipient.getIsRead();
		this.type = notification.getType();
		
		if(NotificationAction.GROUP_ADDED.equals(notificationRecipient.getAction())){
			showGroupName = true;
			showTitle = false;
		}
		if(NotificationAction.POSTED_GROUP.equals(notificationRecipient.getAction())){
			showGroupName = true;
		}
	}


	NotificationType type;

	private Long entityId;

	private Long subEntityId;

	private long id;

	private String title;

	private String sender;

	private String senderName;

	private String senderPosition;

	private long createdTime;

	String comment;

	String action;

	String actionKey;

	boolean isRead;

	Long groupId;
	
	String groupName;
	
	boolean showTitle = true;
	
	boolean showGroupName = false;
	
	
	public NotificationType getType() {
		return type;
	}

	public void setType(NotificationType type) {
		this.type = type;
	}
	public boolean getShowTitle() {
		return showTitle;
	}

	public boolean getShowGroupName() {
		return showGroupName;
	}
	public boolean getsShowTitle() {
		return showTitle;
	}

	public void setShowTitle(boolean showTitle) {
		this.showTitle = showTitle;
	}

	public boolean getsShowGroupName() {
		return showGroupName;
	}

	public void setShowGroupName(boolean showGroupName) {
		this.showGroupName = showGroupName;
	}

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public Long getSubEntityId() {
		return subEntityId;
	}

	public void setSubEntityId(Long subEntityId) {
		this.subEntityId = subEntityId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getSenderPosition() {
		return senderPosition;
	}

	public void setSenderPosition(String senderPosition) {
		this.senderPosition = senderPosition;
	}


	public long getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getActionKey() {
		return actionKey;
	}

	public void setActionKey(String actionKey) {
		this.actionKey = actionKey;
	}

	public boolean getIsRead() {
		return isRead;
	}

	public void setRead(boolean isRead) {
		this.isRead = isRead;
	}

	public long getGroupId() {
		if(null == groupId){
			return 0;
		}
		return groupId;
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}
	
	
}
