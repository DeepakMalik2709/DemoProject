package com.notes.nicefact.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import org.apache.commons.lang.StringUtils;

import com.notes.nicefact.enums.NotificationAction;



/**
 * @author JKB
 * people  Notificatio is shared with
 */
@Entity
public class NotificationRecipient extends CommonEntity{
	private static final long serialVersionUID = 1L;

	//Ancestor
	@ManyToOne(fetch=FetchType.LAZY)
	private Notification notification;
	
	Boolean isRead = false;
	
	String email;
	
	String name;
	
	Boolean sendEmail = true;
	
	Boolean sendToUI = true;
	
	
	@Enumerated(EnumType.STRING)
	private NotificationAction action ;
	

	public NotificationAction getAction() {
		return action;
	}

	public void setAction(NotificationAction action) {
		this.action = action;
	}

	public NotificationRecipient(){
		
	}
	
	public NotificationRecipient(String email2) {
		this.email = email2;
	}

	/* user is person who will receive notification */
	public NotificationRecipient(AppUser user) {
		this.email = user.getEmail();
		this.name = user.getDisplayName();
	}

	public Notification getNotification() {
		return notification;
	}

	public void setNotification(Notification notification) {
		this.notification = notification;
	}

	public String getName() {
		if(StringUtils.isBlank(name)){
			return this.getEmail();
		}
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getIsRead() {
		return isRead;
	}

	public void setIsRead(Boolean isRead) {
		this.isRead = isRead;
	}

	public Boolean getSendEmail() {
		return sendEmail;
	}

	public void setSendEmail(Boolean sendEmail) {
		this.sendEmail = sendEmail;
	}

	public Boolean getSendToUI() {
		return sendToUI;
	}

	public void setSendToUI(Boolean sendToUI) {
		this.sendToUI = sendToUI;
	}

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NotificationRecipient other = (NotificationRecipient) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		return true;
	}

	

}
