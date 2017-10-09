package com.notes.nicefact.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import com.notes.nicefact.to.TaskTO;

@Entity
public class Task extends CommonEntity{

	private static final long serialVersionUID = 1L;
	
	public enum TASK_TYPE{
		CLASS_ASSIGNMENT
	}
	
	int noOfSubmissions;
	
	@Basic
	Date deadline;
	
	@Basic
	private Long groupId;

	@ElementCollection(fetch = FetchType.LAZY)
	Set<String> accessList;
	
	@Column(columnDefinition = "TEXT")
	String  comment;
	
	@Basic
	Boolean isEdited = false;
	
	private String zipFilePath;
	
	@OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
	protected List<TaskFile> files = new ArrayList<>();
	

	public Task(TaskTO task) {
		super();
		this.groupId = task.getGroupId();
		this.comment = task.getComment();
		if(task.getDeadlineTime()  >0){
			this.deadline = new Date(task.getDeadlineTime());
		}
	}

	public void updateProps(Task task) {
		this.comment = task.getComment();
		this.isEdited=true;
		if(task.getDeadline()  !=null){
			this.deadline = new Date(task.getDeadline().getTime());
		}
	}
	
	public Task() {
		super();
	}
	
	public List<TaskFile> getFiles() {
		return files;
	}

	public void setFiles(List<TaskFile> files) {
		this.files = files;
	}

	public int getNoOfSubmissions() {
		return noOfSubmissions;
	}

	public void setNoOfSubmissions(int noOfSubmissions) {
		this.noOfSubmissions = noOfSubmissions;
	}

	public Date getDeadline() {
		return deadline;
	}

	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Set<String> getAccessList() {
		return accessList;
	}

	public void setAccessList(Set<String> accessList) {
		this.accessList = accessList;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
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
}
