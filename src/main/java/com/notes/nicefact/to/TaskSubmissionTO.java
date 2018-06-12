package com.notes.nicefact.to;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.notes.nicefact.entity.TaskSubmission;
import com.notes.nicefact.entity.TaskSubmissionFile;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskSubmissionTO {

	private Long postId;

	Long id;
	String comment;
	
	String submitterName;
	
	String submitterEmail;
	
	List<FileTO> files = new ArrayList<>();

	long createdTime;

	long updatedTime;

	
	
	
	public TaskSubmissionTO(TaskSubmission submission) {
		super();
		this.postId = submission.getPostId() ;
		this.id =  submission.getId();
		this.comment = submission.getComment();
		this.createdTime = submission.getCreatedTime().getTime();
		this.updatedTime = submission.getUpdatedTime().getTime();
		
		this.files = getTaskSubmissionFilesTO(submission.getFiles());
		
		
	}

	private List<FileTO> getTaskSubmissionFilesTO(List<TaskSubmissionFile> files) {
		List<FileTO> filesTOList = new ArrayList<>();
		if(files!=null){
			for(TaskSubmissionFile file : files){
				FileTO fileTO = new FileTO();
				fileTO.setName(file.getName());
				fileTO.setId(file.getId());
				fileTO.setServerName(file.getServerName());
				fileTO.setMimeType(file.getMimeType());
				fileTO.setSize(file.getSize());
				fileTO.setIcon(file.getIcon());
				fileTO.setSizeBytes(file.getSizeBytes());
				fileTO.setDownloadCount(file.getDownloadCount());
				fileTO.setThumbnailLink(file.getThumbnail());
				fileTO.setDriveLink(file.getDriveLink());
				fileTO.setEmbedLink(file.getEmbedLink());
				
				filesTOList.add(fileTO);
			}
		}
		
		return filesTOList;
	}

	public TaskSubmissionTO() {
		super();
	}


	public Long getPostId() {
		return postId;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
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
