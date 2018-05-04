package com.notes.nicefact.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PreUpdate;

import com.notes.nicefact.enums.ScheduleAttendeeResponseType;
import com.notes.nicefact.to.PostRecipientTO;

@Entity
public class PostRecipient extends AbstractRecipient {

	private static final long serialVersionUID = 1L;
	// Ancestor
	@ManyToOne(fetch = FetchType.LAZY)
	private Post post;

	@Enumerated(EnumType.STRING)
	private ScheduleAttendeeResponseType scheduleResponse;
	
	public ScheduleAttendeeResponseType getScheduleResponse() {
		return scheduleResponse;
	}

	public void setScheduleResponse(ScheduleAttendeeResponseType scheduleResponse) {
		this.scheduleResponse = scheduleResponse;
	}
	
	public PostRecipient(PostRecipientTO postRecipientTO) {
		this.id = postRecipientTO.getId();
		this.scheduleResponse= ScheduleAttendeeResponseType.valueOf(postRecipientTO.getScheduleResponse());
		this.email=postRecipientTO.getEmail();
		this.type = RecipientType.valueOf(postRecipientTO.getType());
	}
	
	public PostRecipient() {super();}

	public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}

	public void updateProps(PostRecipientTO postRecipientTO) {
		this.scheduleResponse = ScheduleAttendeeResponseType.valueOf(postRecipientTO.getScheduleResponse());
		
	}

	@PreUpdate
	void prePersist(){
		super.preStore();
	}
}