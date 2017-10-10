package com.notes.nicefact.to;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.notes.nicefact.entity.TaskSubmission;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskSubmissionTO {

	private Long taskId;

	Long id;
	String comment;
	
	String submitterName;
	
	String submitterEmail;
	
	List<FileTO> files = new ArrayList<>();

	long createdTime;

	long updatedTime;

	
	
	
	public TaskSubmissionTO(TaskSubmission submission) {
		super();
		this.taskId = submission.getTaskId() ;
		this.id =  submission.getId();
		this.comment = submission.getComment();
		this.createdTime = submission.getCreatedTime().getTime();
		this.updatedTime = submission.getUpdatedTime().getTime();
		//TODO : populate files
		/*
		
		this.name = submission.getCreatedByName();
		this.fileName = submission.getName();
		this.serverName = submission.getServerName();
		this.mimeType = submission.getMimeType();
		this.size = submission.getSize();
		this.icon = submission.getIcon();
		this.sizeBytes = submission.getSizeBytes();
		this.downloadCount = submission.getDownloadCount();
		this.hasThumbnail = StringUtils.isNotBlank(submission.getThumbnail());
		this.thumbnailLink = submission.getThumbnail();
		this.driveLink = submission.getDriveLink();
		this.embedLink = submission.getEmbedLink();
		isDriveFile = StringUtils.isNotBlank(this.driveLink);*/
		
	}

	public TaskSubmissionTO() {
		super();
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getSubmitterName() {
		return submitterName;
	}

	public void setSubmitterName(String submitterName) {
		this.submitterName = submitterName;
	}

	public String getSubmitterEmail() {
		return submitterEmail;
	}

	public void setSubmitterEmail(String submitterEmail) {
		this.submitterEmail = submitterEmail;
	}

	public List<FileTO> getFiles() {
		return files;
	}

	public void setFiles(List<FileTO> files) {
		this.files = files;
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
	
}
