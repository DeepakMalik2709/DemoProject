package com.notes.nicefact.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import com.notes.nicefact.exception.AppException;
import com.notes.nicefact.to.CommentTO;
import com.notes.nicefact.util.CurrentContext;

/**
 * @author JKB
 *	abstract comment class with required fields to form a comment
 */
@MappedSuperclass
public abstract class AbstractComment extends CommonEntity{

	private static final long serialVersionUID = 1L;

	// main text of comment
	@Column(columnDefinition = "TEXT")
	String  comment;
	
	// position of sender in organization
	@Basic
	String senderPosition;
	

	String senderDepartment;
	
	String senderOrganization;
	
	// has comment been edited
	@Basic
	Boolean isEdited = false;
	
	public AbstractComment() {}
	
	public AbstractComment(CommentTO commentTO) throws AppException {
		comment = commentTO.getComment();
		
		if (null == CurrentContext.getAppUser()) {
			throw new AppException(403, "Login User is null");
		}else{
			senderPosition = CurrentContext.getAppUser().getPosition();
			senderDepartment  = CurrentContext.getAppUser().getDepartment();
			senderOrganization = CurrentContext.getAppUser().getOrganization();
		}
	}

	public void updateProps(AbstractComment commentTO){
		comment = commentTO.getComment();
	}


	public Boolean getIsEdited() {
		return isEdited;
	}

	public void setIsEdited(Boolean isEdited) {
		this.isEdited = isEdited;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getSenderDepartment() {
		return senderDepartment;
	}

	public void setSenderDepartment(String senderDepartment) {
		this.senderDepartment = senderDepartment;
	}

	public String getSenderOrganization() {
		return senderOrganization;
	}

	public void setSenderOrganization(String senderOrganization) {
		this.senderOrganization = senderOrganization;
	}



	public String getSenderPosition() {
		return senderPosition;
	}


	public void setSenderPosition(String senderPosition) {
		this.senderPosition = senderPosition;
	}


	

}
