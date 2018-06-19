package com.notes.nicefact.to;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.api.services.calendar.model.EventAttendee;
import com.notes.nicefact.entity.PostComment;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CommentTO {

	Long postId;
	Long commentId;
	Long subCommentId;
	String comment;
	List<PostRecipientTO> recipients = new ArrayList<>();

	String senderPosition;
	String attendeeId;
	String senderDepartment;

	String senderOrganization;

	// has comment been edited
	private Boolean isEdited;
	List<CommentTO> comments = new ArrayList<>();
	int numberOfComments;
	
	int numberOfReactions;

	List<PostReactionTO> reactions = new ArrayList<>();

	int noOfComments;

	long createdTime;

	long updatedTime;

	String createdByEmail;

	String createdByName;

	String updatedByName;

	String updatedByEmail;
	
	String resposeStatus;
	
	private long createdById;

	public String getResposeStatus() {
		return resposeStatus;
	}

	public void setResposeStatus(String resposeStatus) {
		this.resposeStatus = resposeStatus;
	}

	public CommentTO() {
		
	}
	public CommentTO(EventAttendee attendee) {
		super();
		this.attendeeId = attendee.getId();
		this.comment = attendee.getComment();
		this.createdByEmail = attendee.getEmail();
		this.createdByName = attendee.getDisplayName();
		this.updatedByEmail =attendee.getEmail();
		this.updatedByName =attendee.getDisplayName();
		this.resposeStatus=attendee.getResponseStatus();
		
		
	}

	public CommentTO(PostComment postComment) {

		this.comment = postComment.getComment();
		this.createdByEmail = postComment.getCreatedBy();
		this.createdByName = postComment.getCreatedByName();
		this.updatedByEmail = postComment.getUpdatedBy();
		this.updatedByName = postComment.getUpdatedByName();
		this.createdTime = postComment.getCreatedTime().getTime();
		this.updatedTime = postComment.getUpdatedTime().getTime();
		this.numberOfComments = postComment.getNumberOfComments();
		this.numberOfReactions = postComment.getNumberOfReactions();
		if(null!=postComment.getPost()){
			this.postId = postComment.getPost().getId();
			this.commentId = postComment.getId();
		}else if(null != postComment.getParent()){
			this.subCommentId = postComment.getId();
			this.commentId = postComment.getParent().getId();
			this.postId = postComment.getParent().getPost().getId();
		}
		CommentTO subComment ; 
		for(PostComment comm : postComment.getComments()){
			subComment = new CommentTO(comm);
			this.comments.add(subComment);
		}
	}

	public Long getPostId() {
		if(null == postId){
			return -1l;
		}
		return postId;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
	}

	public Long getCommentId() {
		if(null == commentId){
			return -1l;
		}
		return commentId;
	}

	public void setCommentId(Long commentId) {
		this.commentId = commentId;
	}

	public String getAttendeeId() {
		return attendeeId;
	}

	public void setAttendeeId(String attendeeId) {
		this.attendeeId = attendeeId;
	}
	
	public Long getSubCommentId() {
		if(null == subCommentId){
			return -1l;
		}
		return subCommentId;
	}

	public void setSubCommentId(Long subCommentId) {
		this.subCommentId = subCommentId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}


	public List<PostRecipientTO> getRecipients() {
		return recipients;
	}

	public void setRecipients(List<PostRecipientTO> recipients) {
		this.recipients = recipients;
	}

	public String getSenderPosition() {
		return senderPosition;
	}

	public void setSenderPosition(String senderPosition) {
		this.senderPosition = senderPosition;
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

	public Boolean getIsEdited() {
		return isEdited;
	}

	public void setIsEdited(Boolean isEdited) {
		this.isEdited = isEdited;
	}



	public List<CommentTO> getComments() {
		return comments;
	}

	public void setComments(List<CommentTO> comments) {
		this.comments = comments;
	}

	public int getNumberOfComments() {
		return numberOfComments;
	}

	public void setNumberOfComments(int numberOfComments) {
		this.numberOfComments = numberOfComments;
	}

	public int getNumberOfReactions() {
		return numberOfReactions;
	}

	public void setNumberOfReactions(int numberOfReactions) {
		this.numberOfReactions = numberOfReactions;
	}

	public List<PostReactionTO> getReactions() {
		return reactions;
	}

	public void setReactions(List<PostReactionTO> reactions) {
		this.reactions = reactions;
	}

	public int getNoOfComments() {
		return noOfComments;
	}

	public void setNoOfComments(int noOfComments) {
		this.noOfComments = noOfComments;
	}

	public long getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}

	public long getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(long updatedTime) {
		this.updatedTime = updatedTime;
	}

	public String getCreatedByEmail() {
		return createdByEmail;
	}

	public void setCreatedByEmail(String createdByEmail) {
		this.createdByEmail = createdByEmail;
	}

	public String getCreatedByName() {
		return createdByName;
	}

	public void setCreatedByName(String createdByName) {
		this.createdByName = createdByName;
	}

	public String getUpdatedByName() {
		return updatedByName;
	}

	public void setUpdatedByName(String updatedByName) {
		this.updatedByName = updatedByName;
	}

	public String getUpdatedByEmail() {
		return updatedByEmail;
	}

	public void setUpdatedByEmail(String updatedByEmail) {
		this.updatedByEmail = updatedByEmail;
	}

	public long getCreatedById() {
		return createdById;
	}

	public void setCreatedById(long createdById) {
		this.createdById = createdById;
	}
}