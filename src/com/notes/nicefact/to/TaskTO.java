package com.notes.nicefact.to;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.notes.nicefact.entity.Task;

/**
 * @author JKB DTO to send / get Task state
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskTO {

	Long id;
	Long groupId;
	String groupName;
	String comment;

	long createdTime;

	long updatedTime;

	int noOfSubmissions;

	long deadlineTime;

	String createdByEmail;

	String createdByName;

	String updatedByName;

	String updatedByEmail;

	Boolean isEdited = false;

	private String zipFilePath;

	List<TaskSubmissionTO> submissions = new ArrayList<>();

	
	
	
	public TaskTO(Task task) {
		super();
		this.id = task.getId() ;
		this.groupId = task.getGroupId();
		this.comment = task.getComment();
		this.createdTime = task.getCreatedTime().getTime();
		this.updatedTime = task.getUpdatedTime().getTime();
		this.noOfSubmissions = task.getNoOfSubmissions();
		if(null != task.getDeadline()){
			this.deadlineTime = task.getDeadline().getTime();
		}
		this.createdByEmail = task.getCreatedBy();
		this.createdByName = task.getCreatedByName();
		this.updatedByName = task.getUpdatedByName();
		this.updatedByEmail = task.getUpdatedBy();
		this.isEdited = task.getIsEdited();
		this.zipFilePath = task.getZipFilePath();
	}

	public TaskTO() {
		super();
	}

	public Long getId() {
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

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
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

	public int getNoOfSubmissions() {
		return noOfSubmissions;
	}

	public void setNoOfSubmissions(int noOfSubmissions) {
		this.noOfSubmissions = noOfSubmissions;
	}

	public long getDeadlineTime() {
		return deadlineTime;
	}

	public void setDeadlineTime(long deadlineTime) {
		this.deadlineTime = deadlineTime;
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

	public Boolean getIsEdited() {
		return isEdited;
	}

	public void setIsEdited(Boolean isEdited) {
		this.isEdited = isEdited;
	}

	public String getZipFilePath() {
		return zipFilePath;
	}

	public void setZipFilePath(String zipFilePath) {
		this.zipFilePath = zipFilePath;
	}

	public List<TaskSubmissionTO> getSubmissions() {
		return submissions;
	}

	public void setSubmissions(List<TaskSubmissionTO> submissions) {
		this.submissions = submissions;
	}

}
