package com.notes.nicefact.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import com.notes.nicefact.to.TaskSubmissionTO;

@Entity
public class TaskSubmission  extends CommonEntity{

	private static final long serialVersionUID = 1L;
	
	@Column(columnDefinition = "TEXT")
	String  comment;

	private Long postId;
	
	private String submitterName;
	
	private String submitterEmail;
	
	private Date submitDate = new Date();
	
	@OneToMany(mappedBy="submission", cascade=CascadeType.ALL)
	List<TaskSubmissionFile> files = new ArrayList<>();
	
	public TaskSubmission() {
		super();
	}

	public TaskSubmission(TaskSubmissionTO submission) {
		super();
		this.comment = submission.getComment();
		this.postId = submission.getPostId();
		
	}
	public Long getPostId() {
		return postId;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public List<TaskSubmissionFile> getFiles() {
		return files;
	}

	public void setFiles(List<TaskSubmissionFile> files) {
		this.files = files;
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

	public Date getSubmitDate() {
		return submitDate;
	}

	public void setSubmitDate(Date submitDate) {
		this.submitDate = submitDate;
	}
	
}
