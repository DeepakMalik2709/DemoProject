package com.notes.nicefact.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.api.services.calendar.model.EventAttendee;
import com.notes.nicefact.to.FileTO;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Event {

	private long postId;
	private String id;
	private String title;
	private Date start;
	private String backgroundColor;
	private String borderColor;
	private Date end;
	private String description;
	private String location;
	private String	eventType;
	private List<EventAttendee>	attendees;
	private List<Group> groups; 
	private long groupId;
	private String comment;
	private String createdByEmail;
	private String createdByName;
	private String updatedByEmail;
	private String updatedByName;
	private List<String> weekdays;
	private long createdTime;
	private long updatedTime;
	private int numberOfReactions;
	private List<PostComment> comments = new ArrayList<>();
	private List<FileTO> files = new ArrayList<>();
	private String googleEventId;
	public String getGoogleEventId() {
		return googleEventId;
	}
	public void setGoogleEventId(String googleEventId) {
		this.googleEventId = googleEventId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Date getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start;
	}
	public String getBackgroundColor() {
		return backgroundColor;
	}
	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	public String getBorderColor() {
		return borderColor;
	}
	public void setBorderColor(String borderColor) {
		this.borderColor = borderColor;
	}
	public Date getEnd() {
		return end;
	}
	public void setEnd(Date end) {
		this.end = end;
	}


	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public List<EventAttendee> getAttendees() {
		return attendees;
	}
	public void setAttendees(List<EventAttendee> attendees) {
		this.attendees = attendees;
	}
	public List<Group> getGroups() {
		return groups;
	}
	public void setGroups(List<Group> groups) {
		this.groups = groups;
	}
	public long getPostId() {
		return postId;
	}
	public void setPostId(long postId) {
		this.postId = postId;
	}
	public long getGroupId() {
		return groupId;
	}
	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
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
	public String getUpdatedByEmail() {
		return updatedByEmail;
	}
	public void setUpdatedByEmail(String updatedByEmail) {
		this.updatedByEmail = updatedByEmail;
	}
	public String getUpdatedByName() {
		return updatedByName;
	}
	public void setUpdatedByName(String updatedByName) {
		this.updatedByName = updatedByName;
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
	public int getNumberOfReactions() {
		return numberOfReactions;
	}
	public void setNumberOfReactions(int numberOfReactions) {
		this.numberOfReactions = numberOfReactions;
	}
	public List<PostComment> getComments() {
		return comments;
	}
	public void setComments(List<PostComment> comments) {
		this.comments = comments;
	}
	public List<FileTO> getFiles() {
		return files;
	}
	public void setFiles(List<FileTO> files) {
		this.files = files;
	}
	@Override
	public String toString() {
		return "Event [postId=" + postId + ", id=" + id + ", title=" + title
				+ ", start=" + start + ", backgroundColor=" + backgroundColor
				+ ", borderColor=" + borderColor + ", end=" + end
				+ ", description=" + description + ", location=" + location
				+ ", eventType=" + eventType + ", attendees=" + attendees
				+ ", groups=" + groups + ", groupId=" + groupId + ", comment="
				+ comment + ", createdByEmail=" + createdByEmail
				+ ", createdByName=" + createdByName + ", updatedByEmail="
				+ updatedByEmail + ", updatedByName=" + updatedByName
				+ ", createdTime=" + createdTime + ", updatedTime="
				+ updatedTime + ", numberOfReactions=" + numberOfReactions
				+ ", comments=" + comments + ", files=" + files + "]";
	}
	public List<String> getWeekdays() {
		return weekdays;
	}
	public void setWeekdays(List<String> weekdays) {
		this.weekdays = weekdays;
	}

}
