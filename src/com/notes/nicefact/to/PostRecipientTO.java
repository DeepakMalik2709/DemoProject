package com.notes.nicefact.to;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.notes.nicefact.entity.PostRecipient;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PostRecipientTO {

	Long id;

	String email;
	
	String label;

	Long postId;
	
	String scheduleResponse;

	String type;
		
	public PostRecipientTO(PostRecipient postRecipient) {
		this.id=postRecipient.getId();
		this.email = postRecipient.getEmail();
		if(postRecipient.getScheduleResponse()!=null){
			this.scheduleResponse = postRecipient.getScheduleResponse().name();	
		}		
		if(postRecipient.getPost()!=null){
			this.postId=postRecipient.getPost().getId();	
		}
		if(postRecipient.getType() != null){
			this.type = postRecipient.getType().name();
		}
	}
	public PostRecipientTO() {		
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public String getScheduleResponse() {
		return scheduleResponse;
	}

	public void setScheduleResponse(String scheduleResponse) {
		this.scheduleResponse = scheduleResponse;
	}

	public Long getPostId() {
		return postId;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getType() {
		return type;
	}
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
