package com.notes.nicefact.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;

import com.notes.nicefact.entity.Post.POST_TYPE;
import com.notes.nicefact.enums.NotificationType;
import com.notes.nicefact.enums.SHARING;
import com.notes.nicefact.util.CacheUtils;

/**
 * @author JKB
 *
 * Notifications for updates
 */
@Entity
public class Notification extends CommonEntity{
	private static final long serialVersionUID = 1L;

	//tile of Notification
	@Basic
	private String title;
		
	//  text of comment
	@Basic
	protected String comment;
	
	// people Notification is shared with
	@OneToMany(mappedBy="notification", cascade=CascadeType.ALL)
    private List<NotificationRecipient> recipients = new ArrayList<>();
	
	
	//email id of user creating comment
	@Basic String senderName;
	
	//email id of user creating comment
	@Basic String sender;
	
	// position of sender in organization
	@Basic
	private String senderPosition;

	// key of entity which generated notification
	@Basic
	private Long entityId;
	
	@Basic
	private Long subEntityId;
	
	@Basic
	private Long groupId;
	
	// name of group
	@Basic String groupName;;
	
	@Enumerated(EnumType.STRING)
	private SHARING sharing;
	
	@Enumerated(EnumType.STRING)
	NotificationType type;

	public Notification(){}
	
	/* user is person generating notification , i.e. logged in user*/
	public Notification(AppUser user){
		sender =  user.getEmail();
		senderPosition = user.getPosition();
		senderName = user.getDisplayName();
	}


	private String stripEmailFromComments(String comment){
		String commentStr = "";
		if(StringUtils.isNotBlank(comment)){
			commentStr = comment.replaceAll("\\(.*?@.*?\\)","");
		}
		commentStr = Jsoup.parse( commentStr).text();
		return commentStr;
	}
	
	public Notification(Post post, AppUser user) {
		this(user);
		entityId = post.getId();
		groupId = post.getGroupId();
		if (null != groupId) {
			Group group = CacheUtils.getGroup(groupId);
			groupName = group.getName();
		}

		sharing = post.getSharing();
		if (POST_TYPE.TASK.equals(post.getPostType())) {
			this.type = NotificationType.TASK;
		} else {
			this.type = NotificationType.POST;
		}
		if (StringUtils.isBlank(post.getTitle())) {

			this.title = stripEmailFromComments(post.getComment());
			if (this.title.length() > 150) {
				this.title = this.title.substring(0, 150);
			}
		} else {
			this.title = post.getTitle();
		}
	}

	public Notification(Post post , PostComment comment , AppUser user) {
		this(post, user);
		this.subEntityId = comment.getId();
		this.title = stripEmailFromComments(comment.getComment());
		this.type = comment.getParent() == null? NotificationType.COMMENT : NotificationType.COMMENT_REPLY;
		if(this.title.length() > 150){
			this.title = this.title.substring(0, 150);
		}
	}

	
	public NotificationType getType() {
		return type;
	}

	public void setType(NotificationType type) {
		this.type = type;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}



	public List<NotificationRecipient> getRecipients() {
		return recipients;
	}

	public void setRecipients(List<NotificationRecipient> recipients) {
		this.recipients = recipients;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getSenderPosition() {
		return senderPosition;
	}

	public void setSenderPosition(String senderPosition) {
		this.senderPosition = senderPosition;
	}


	public SHARING getSharing() {
		return sharing;
	}

	public void setSharing(SHARING sharing) {
		this.sharing = sharing;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
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

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	
}
