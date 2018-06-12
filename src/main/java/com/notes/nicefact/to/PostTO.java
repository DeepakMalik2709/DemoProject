package com.notes.nicefact.to;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttachment;
import com.google.api.services.calendar.model.EventAttendee;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.Group;
import com.notes.nicefact.entity.Post;
import com.notes.nicefact.entity.Post.POST_TYPE;
import com.notes.nicefact.entity.PostComment;
import com.notes.nicefact.entity.PostFile;
import com.notes.nicefact.entity.PostRecipient;
import com.notes.nicefact.entity.PostTag;
import com.notes.nicefact.enums.ScheduleAttendeeResponseType;
import com.notes.nicefact.util.CacheUtils;
import com.notes.nicefact.util.CurrentContext;

/**
 * @author JKB DTO to send / get Post state
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostTO {

	Long id;
	String eventId;
	Long groupId;
	String groupName;
	String title;
	String comment;
	String location;
	List<Long> groupIds = new ArrayList<>();
	List<TagTO> tags = new ArrayList<>();
	
	long createdTime;
	
	long updatedTime;
	
	String createdByEmail;
	
	String createdByName;
	
	String updatedByName;
	
	String updatedByEmail;
	
	String postPriv;

	// List<GoogleDriveFileTO> files = new ArrayList<>() ;
	
	private String newTag;
	private String postCategory;
	
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
	long fromDate;
	long toDate;
	private List<String> weekdays;
	
	int noOfSubmissions;
	Boolean allDayEvent;
	long deadlineTime;
	private String googleEventId;
	public String getGoogleEventId() {
		return googleEventId;
	}
	public void setGoogleEventId(String googleEventId) {
		this.googleEventId = googleEventId;
	}

	Boolean isEdited = false;
	
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	Boolean isSubmitted = false;
	
	Boolean canSubmit = false;
	
	List<TaskSubmissionTO> submissions = new ArrayList<>();

	
	public PostTO(){
		
	}
	
/*	public PostTO(com.notes.nicefact.to.Event schedule, AppUser user) {
		this.id = schedule.getPostId();
		this.groupId = schedule.getGroupId();
		this.postType = POST_TYPE.SCHEDULE;
		if(groupId !=null && groupId > 0){
			Group group = CacheUtils.getGroup(this.groupId);
			this.groupName =  group.getName();
		}
		this.location = schedule.getLocation();
		this.comment = schedule.getComment();
		if(this.comment ==null){
			this.comment = schedule.getDescription();
		}
		this.fromDate = schedule.getStart().getTime();
		this.toDate = schedule.getEnd().getTime();
		this.createdByEmail = schedule.getCreatedByEmail();
		this.createdByName = schedule.getCreatedByName();
		this.updatedByEmail = schedule.getUpdatedByEmail();
		this.updatedByName = schedule.getUpdatedByName();
		this.createdTime = schedule.getCreatedTime();
		this.updatedTime = schedule.getUpdatedTime();
		this.numberOfReactions = schedule.getNumberOfReactions();
		CommentTO commentTO;
		for (PostComment comment : schedule.getComments()) {
			commentTO = new CommentTO(comment);
			this.comments.add(commentTO);
		}
		
		for(FileTO file : schedule.getFiles()){
			
			this.files.add(file);
		}
		this.title = schedule.getTitle();
		this.googleEventId=schedule.getGoogleEventId();
	}*/
	public PostTO(Post post) {
		this.id = post.getId();
		this.groupId = post.getGroupId();
		this.postType = post.getPostType();
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
		if(POST_TYPE.SCHEDULE.equals(post.getPostType()) ){	
			this.setScheduleAttribute(post);
		}
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
		this.title = post.getTitle();
		this.noOfSubmissions = post.getNoOfSubmissions();
		if(null != post.getDeadline()){
			this.deadlineTime = post.getDeadline().getTime();
			this.canSubmit = this.deadlineTime > new Date().getTime();
		}else{
			this.canSubmit = true;
		}
		if(CurrentContext.getAppUser() !=null){
			this.isSubmitted = post.getSubmitters().contains(CurrentContext.getEmail());
		}
		
		if(post.getPostTags().size() > 0) {
			TagTO tagTO = null;
			List<String> tagNames = new ArrayList<>();
			
			for(PostTag postTag: post.getPostTags()) {
				tagTO = new TagTO();
				tagTO.setId(postTag.getTag().getId());
				tagTO.setName(postTag.getTag().getName());
				this.tags.add(tagTO);
				tagNames.add(postTag.getTag().getName());
			}
			
			this.newTag = StringUtils.join(tagNames, ',');
		}
		
		if(post.getPostCategory() == null || post.getPostCategory().equals("")) {
			this.postCategory = "PRIVATE";
		} else {
			this.postCategory = post.getPostCategory().name();
		}
	}

	private void setScheduleAttribute(Post post) {
		
		AppUser user =  CurrentContext.getAppUser();
		if(this.createdByEmail.equalsIgnoreCase(user.getEmail())){
			this.postPriv="creator";		
			if(post.getRecipients()!=null  ){
				this.totalAttendee = post.getRecipients().size();
				for (PostRecipient postRecipient :  post.getRecipients()) {				
						if(postRecipient.getScheduleResponse().equals(ScheduleAttendeeResponseType.ACCEPTED)){
							this.reponseYes++;
						}else if(postRecipient.getScheduleResponse().equals(ScheduleAttendeeResponseType.TENTATIVE)){
							this.reponseMaybe++;
						}else if(postRecipient.getScheduleResponse().equals(ScheduleAttendeeResponseType.DECLINED)){
							this.reponseNo++;
						}
				}
			}
		}else{
			this.postPriv="attendee";	
			if(post.getRecipients()!=null  ){					
				for (PostRecipient postRecipient :  post.getRecipients()) {		
					if(postRecipient.getEmail().equalsIgnoreCase(user.getEmail())){
						recipients.add(new PostRecipientTO(postRecipient));
					}
				}
			}
		}
		this.googleEventId = post.getGoogleEventId();
		this.location = post.getLocation();
		if(null !=post.getFromDate()){
			this.fromDate = post.getFromDate().getTime();
		}
		if(null !=post.getToDate()){
			this.toDate = post.getToDate().getTime();
		}
		this.allDayEvent = post.getAllDayEvent();
		this.weekdays = new ArrayList<>(post.getWeekdays());
	}

	private POST_TYPE postType = POST_TYPE.SIMPLE;
	
	public PostTO(Event event, AppUser user) {
		this.eventId =event.getId();
		
		this.postType = POST_TYPE.SCHEDULE;
		this.comment = event.getSummary()+" " + event.getDescription()+" "+event.getStart().getDateTime()+"-"+event.getEnd().getDateTime();
		this.createdByEmail = event.getCreator().getEmail();
		this.createdByName = event.getCreator().getDisplayName();
		this.updatedByEmail =  event.getCreator().getEmail();
		this.updatedByName = event.getCreator().getDisplayName();
		this.createdTime = event.getCreated().getValue();
		this.updatedTime = event.getUpdated().getValue();
		if(this.createdByEmail.equalsIgnoreCase(user.getEmail())){
			this.postPriv="creator";
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
					if(attendee.getComment()!=null){
						commentTO = new CommentTO(attendee);
						this.comments.add(commentTO);
					}
				
				
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
	
	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public POST_TYPE getPostType() {
		return postType;
	}

	public void setPostType(POST_TYPE postType) {
		this.postType = postType;
	}

	public String getPostPriv() {
		return postPriv;
	}

	public void setPostPriv(String postPriv) {
		this.postPriv = postPriv;
	}

	public String getNewTag() {
		return newTag;
	}
	public void setNewTag(String newTag) {
		this.newTag = newTag;
	}
	
	public String getPostCategory() {
		return postCategory;
	}
	
	public void setPostCategory(String postCategory) {
		this.postCategory = postCategory;
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
	public boolean getIsPost(){
		return null == this.postType ||  POST_TYPE.SIMPLE.equals(this.postType);
	}
	
	public boolean getIsTask(){
		return POST_TYPE.TASK.equals(this.postType);
	}
	
	public boolean getIsSchedule(){
		return POST_TYPE.SCHEDULE.equals(this.postType);
	}
	
	public long getDeadlineTime() {
		return deadlineTime;
	}

	public void setDeadlineTime(long deadlineTime) {
		this.deadlineTime = deadlineTime;
	}
	public int getNoOfSubmissions() {
		return noOfSubmissions;
	}
	public void setNoOfSubmissions(int noOfSubmissions) {
		this.noOfSubmissions = noOfSubmissions;
	}
	public Boolean getIsEdited() {
		return isEdited;
	}
	public void setIsEdited(Boolean isEdited) {
		this.isEdited = isEdited;
	}
	public Boolean getIsSubmitted() {
		return isSubmitted;
	}
	public void setIsSubmitted(Boolean isSubmitted) {
		this.isSubmitted = isSubmitted;
	}
	public List<TaskSubmissionTO> getSubmissions() {
		return submissions;
	}
	public void setSubmissions(List<TaskSubmissionTO> submissions) {
		this.submissions = submissions;
	}
	public List<Long> getGroupIds() {
		return groupIds;
	}
	public void setGroupIds(List<Long> groupIds) {
		this.groupIds = groupIds;
	}
	public Boolean getCanSubmit() {
		return canSubmit;
	}
	public void setCanSubmit(Boolean canSubmit) {
		this.canSubmit = canSubmit;
	}
	public long getFromDate() {
		return fromDate;
	}
	public void setFromDate(long fromDate) {
		this.fromDate = fromDate;
	}
	public long getToDate() {
		return toDate;
	}
	public void setToDate(long toDate) {
		this.toDate = toDate;
	}
	public List<String> getWeekdays() {
		return weekdays;
	}
	public void setWeekdays(List<String> weekdays) {
		this.weekdays = weekdays;
	}
	public Boolean getAllDayEvent() {
		return allDayEvent;
	}
	public void setAllDayEvent(Boolean allDayEvent) {
		this.allDayEvent = allDayEvent;
	}
	
}
