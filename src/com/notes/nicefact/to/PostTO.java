package com.notes.nicefact.to;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttachment;
import com.google.api.services.calendar.model.EventAttendee;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.Group;
import com.notes.nicefact.entity.Post;
import com.notes.nicefact.entity.PostComment;
import com.notes.nicefact.entity.PostFile;
import com.notes.nicefact.enums.ScheduleAttendeeResponseType;
import com.notes.nicefact.util.CacheUtils;

/**
 * @author JKB DTO to send / get Post state
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostTO {

	

	Long id;
	String eventId;
	Long groupId;
	String groupName;
	String comment;

	List<TagTO> tags = new ArrayList<>();
	
	long createdTime;
	
	long updatedTime;
	
	String createdByEmail;
	
	String createdByName;
	
	String updatedByName;
	
	String updatedByEmail;
	
	String postType;
	
	String postPriv;

	// List<GoogleDriveFileTO> files = new ArrayList<>() ;

	public String getPostType() {
		return postType;
	}

	public void setPostType(String postType) {
		this.postType = postType;
	}

	List<PostRecipientTO> recipients = new ArrayList<>();
	
	List<PostReactionTO> reactions = new ArrayList<>();
	
	List<CommentTO> comments = new ArrayList<>();
	
	List<FileTO> files = new ArrayList<>();
	
	int numberOfComments;
	
	int numberOfReactions;
	
	int reponseYes=0;
	int reponseNo=0;
	int reponseMaybe=0;
	int totalAttendee=0;
	public PostTO() {}
	public PostTO(Post post) {
		this.id = post.getId();
		this.groupId = post.getGroupId();
		if(groupId !=null && groupId > 0){
			Group group = CacheUtils.getGroup(this.groupId);
			this.groupName =  group.getName();
		}
		this.comment = post.getComment();
		this.createdByEmail = post.getCreatedBy();
		this.createdByName = post.getCreatedByName();
		this.updatedByEmail = post.getUpdatedBy();
		this.updatedByName = post.getUpdatedByName();
		this.createdTime = post.getCreatedTime().getTime();
		this.updatedTime = post.getUpdatedTime().getTime();
		this.numberOfComments = post.getNumberOfComments();
		this.numberOfReactions = post.getNumberOfReactions();
		CommentTO commentTO;
		for (PostComment comment : post.getComments()) {
			commentTO = new CommentTO(comment);
			this.comments.add(commentTO);
		}
		FileTO fileTO;
		for(PostFile file : post.getFiles()){
			fileTO= new FileTO(file);
			this.files.add(fileTO);
		}
	}

	public PostTO(Event event, AppUser user) {
		this.eventId =event.getId();
		
		if(groupId !=null && groupId > 0){
			Group group = CacheUtils.getGroup(this.groupId);
			this.groupName =  group.getName();
		}
		this.postType = "schedule";
		this.comment = event.getDescription()+" "+event.getStart()+"-"+event.getEnd();
		this.createdByEmail = event.getCreator().getEmail();
		this.createdByName = event.getCreator().getDisplayName();
		this.updatedByEmail =  event.getCreator().getEmail();
		this.updatedByName = event.getCreator().getDisplayName();
		this.createdTime = event.getCreated().getValue();
		this.updatedTime = event.getUpdated().getValue();
		if(this.createdByEmail.equalsIgnoreCase(user.getEmail())){
			postPriv="creator";
		}else{
			postPriv="attendee";
		}
		if(event.getAttendees()!=null){
			this.totalAttendee = event.getAttendees().size();
			this.numberOfReactions = event.getAttendees().size();
			CommentTO commentTO;
			for (EventAttendee attendee :  event.getAttendees()) {
				
					if( ScheduleAttendeeResponseType.ACCEPTED.toString().equalsIgnoreCase(attendee.getResponseStatus())){
						this.reponseYes++;
					}else if(ScheduleAttendeeResponseType.TENTATIVE.toString().equalsIgnoreCase(attendee.getResponseStatus())){
						this.reponseMaybe++;
					}else if(ScheduleAttendeeResponseType.DECLINED.toString().equalsIgnoreCase(attendee.getResponseStatus())){
						this.reponseNo++;
					}					
				commentTO = new CommentTO(attendee);
				this.comments.add(commentTO);
			}
		}
		
		FileTO fileTO;
		if(event.getAttachments()!=null){
		for(EventAttachment attach : event.getAttachments()){
			fileTO= new FileTO(attach);
			this.files.add(fileTO);
		}
		}
	}
	
	public String getPostPriv() {
		return postPriv;
	}

	public void setPostPriv(String postPriv) {
		this.postPriv = postPriv;
	}

	public int getTotalAttendee() {
		return totalAttendee;
	}

	public void setTotalAttendee(int totalAttendee) {
		this.totalAttendee = totalAttendee;
	}

	public List<TagTO> getTags() {
		return tags;
	}

	public void setTags(List<TagTO> tags) {
		this.tags = tags;
	}


	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Long getId() {
		if(null == id){
			return -1l;
		}
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
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


	public List<FileTO> getFiles() {
		return files;
	}

	public void setFiles(List<FileTO> files) {
		this.files = files;
	}

	public List<PostRecipientTO> getRecipients() {
		return recipients;
	}

	public void setRecipients(List<PostRecipientTO> recipients) {
		this.recipients = recipients;
	}

	public List<PostReactionTO> getReactions() {
		return reactions;
	}

	public void setReactions(List<PostReactionTO> reactions) {
		this.reactions = reactions;
	}

	public List<CommentTO> getComments() {
		return comments;
	}

	public void setComments(List<CommentTO> comments) {
		this.comments = comments;
	}


	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getReponseYes() {
		return reponseYes;
	}

	public void setReponseYes(int reponseYes) {
		this.reponseYes = reponseYes;
	}

	public int getReponseNo() {
		return reponseNo;
	}

	public void setReponseNo(int reponseNo) {
		this.reponseNo = reponseNo;
	}

	public int getReponseMaybe() {
		return reponseMaybe;
	}

	public void setReponseMaybe(int reponseMaybe) {
		this.reponseMaybe = reponseMaybe;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	public boolean isPost(){
		return true;
	}
}
