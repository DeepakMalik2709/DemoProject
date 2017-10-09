package com.notes.nicefact.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.notes.nicefact.to.TaskSubmissionTO;

@Entity
public class TaskSubmission  extends AbstractFile{

	private static final long serialVersionUID = 1L;
	
	@Column(columnDefinition = "TEXT")
	String  comment;

	private Long taskId ;
	
	public TaskSubmission() {
		super();
	}

	public TaskSubmission(TaskSubmissionTO file, String path) {
		super();
		this.name = file.getName();
		this.serverName = file.getServerName();
		this.mimeType = file.getMimeType();
		this.size = file.getSize();
		this.sizeBytes = file.getSizeBytes();
		this.path = path;
		this.comment = file.getComment();
		this.taskId = file.getTaskId();
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
}
