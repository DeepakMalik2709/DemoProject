package com.notes.nicefact.to;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

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
		this.instituteId = notification.getInstituteId();
		this.groupName = notification.getGroupName();
		this.action = notificationRecipient.getAction().toString();
		this.actionKey = notificationRecipient.getAction().getMessageKey();
		this.isRead = notificationRecipient.getIsRead();
		this.type = notification.getType();
		List<NotificationAction> showGroupNameActions = Arrays.asList(NotificationAction.POSTED_GROUP , NotificationAction.INSTITUTE_ADDED , NotificationAction.INSTITUTE_JOIN_APPROVED, NotificationAction.INSTITUTE_JOIN_REQUESTED,
				NotificationAction.GROUP_ADDED , NotificationAction.GROUP_JOIN_APPROVED, NotificationAction.GROUP_JOIN_REQUESTED );
		
		if(showGroupNameActions.contains(notificationRecipient.getAction())){
			showGroupName = true;
		}
		
		List<NotificationAction> hideTitleActions = Arrays.asList(NotificationAction.INSTITUTE_ADDED , NotificationAction.INSTITUTE_JOIN_APPROVED, NotificationAction.INSTITUTE_JOIN_REQUESTED,
				NotificationAction.GROUP_ADDED , NotificationAction.GROUP_JOIN_APPROVED, NotificationAction.GROUP_JOIN_REQUESTED);
		
		if(hideTitleActions.contains(notificationRecipient.getAction())){
			showTitle = false;
		}
		
		List<NotificationAction> showDetailPageActions = Arrays.asList( NotificationAction.INSTITUTE_JOIN_REQUESTED,
				 NotificationAction.GROUP_JOIN_REQUESTED);
		
		if(showDetailPageActions.contains(notificationRecipient.getAction())){
			showDetailpage = true;
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
	
	boolean showDetailpage = false;

	Long groupId;
	
	String groupName;
	
	boolean showTitle = true;
	
	boolean showGroupName = false;
	
	Long instituteId;
	
	
	/* item notifcation is related to, eg PostComment */
	Object item;
	
	
	
	public Object getItem() {
		return item;
	}

	public void setItem(Object item) {
		this.item = item;
	}

	public Long getInstituteId() {
		if(null == instituteId){
			return 0l;
		}
		return instituteId;
	}

	public void setInstituteId(Long instituteId) {
		this.instituteId = instituteId;
	}
	
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

	public void setShowTitle(boolean showTitle) {
		this.showTitle = showTitle;
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

	public Long getGroupId() {
		if(null == groupId){
			return 0l;
		}
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	
	public boolean getIsPost(){
		return   NotificationType.POST.equals(this.type);
	}
	
	public boolean getIsTask(){
		return  NotificationType.TASK.equals(this.type);
	}
	
	public boolean getIsSchedule(){
		return  NotificationType.SCHEDULE.equals(this.type);
	}

	public boolean getShowDetailpage() {
		return showDetailpage;
	}

	public void setShowDetailpage(boolean showDetailpage) {
		this.showDetailpage = showDetailpage;
	}

	
}
