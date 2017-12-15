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
	
	@Basic
	private Long instituteId;
	
	// name of group or institute
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

	public Notification setType(NotificationType type) {
		this.type = type;
		return this;
	}

	public String getGroupName() {
		return groupName;
	}

	public Notification setGroupName(String groupName) {
		this.groupName = groupName;
		return this;
	}

	public String getSenderName() {
		return senderName;
	}

	public Notification setSenderName(String senderName) {
		this.senderName = senderName;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public Notification setTitle(String title) {
		this.title = title;
		return this;
	}



	public List<NotificationRecipient> getRecipients() {
		return recipients;
	}

	public Notification setRecipients(List<NotificationRecipient> recipients) {
		this.recipients = recipients;
		return this;
	}

	public String getSender() {
		return sender;
	}

	public Notification setSender(String sender) {
		this.sender = sender;
		return this;
	}

	public String getSenderPosition() {
		return senderPosition;
	}

	public Notification setSenderPosition(String senderPosition) {
		this.senderPosition = senderPosition;
		return this;
	}


	public SHARING getSharing() {
		return sharing;
	}

	public Notification setSharing(SHARING sharing) {
		this.sharing = sharing;
		return this;
	}

	public String getComment() {
		return comment;
	}

	public Notification setComment(String comment) {
		this.comment = comment;
		return this;
	}

	public Long getEntityId() {
		return entityId;
	}

	public Notification setEntityId(Long entityId) {
		this.entityId = entityId;
		return this;
	}

	public Long getSubEntityId() {
		return subEntityId;
	}

	public Notification setSubEntityId(Long subEntityId) {
		this.subEntityId = subEntityId;
		return this;
	}

	public Long getGroupId() {
		return groupId;
	}

	public Notification setGroupId(Long groupId) {
		this.groupId = groupId;
		return this;
	}

	public Long getInstituteId() {
		return instituteId;
	}

	public Notification setInstituteId(Long instituteId) {
		this.instituteId = instituteId;
		return this;
	}
}
