package com.notes.nicefact.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import com.notes.nicefact.to.FileTO;

@Entity
public class TaskSubmissionFile  extends AbstractFile{

	private static final long serialVersionUID = 1L;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private TaskSubmission submission;
	
	private Long taskId ;
	
	public TaskSubmissionFile() {
		super();
	}

	public TaskSubmissionFile(FileTO file, Long taskId, String path) {
		super(file,path);
		this.taskId = taskId;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public TaskSubmission getSubmission() {
		return submission;
	}

	public void setSubmission(TaskSubmission submission) {
		this.submission = submission;
	}

	
}
