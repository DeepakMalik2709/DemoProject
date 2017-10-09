package com.notes.nicefact.to;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.notes.nicefact.entity.TaskSubmission;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskSubmissionTO {

	private Long taskId;

	Long id;
	String comment;
	String name;

	String fileName;

	String serverName;

	String mimeType;

	String size;

	String icon;

	long sizeBytes;

	long downloadCount = 0;
	boolean hasThumbnail;
	String thumbnailLink;

	String driveLink;

	String embedLink;
	boolean isDriveFile;

	long createdTime;

	long updatedTime;

	
	
	
	public TaskSubmissionTO(TaskSubmission submission) {
		super();
		this.taskId = submission.getTaskId() ;
		this.id =  submission.getId();
		this.comment = submission.getComment();
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
		isDriveFile = StringUtils.isNotBlank(this.driveLink);
		this.createdTime = submission.getCreatedTime().getTime();
		this.updatedTime = submission.getUpdatedTime().getTime();
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public long getSizeBytes() {
		return sizeBytes;
	}

	public void setSizeBytes(long sizeBytes) {
		this.sizeBytes = sizeBytes;
	}

	public long getDownloadCount() {
		return downloadCount;
	}

	public void setDownloadCount(long downloadCount) {
		this.downloadCount = downloadCount;
	}

	public boolean isHasThumbnail() {
		return hasThumbnail;
	}

	public void setHasThumbnail(boolean hasThumbnail) {
		this.hasThumbnail = hasThumbnail;
	}

	public String getDriveLink() {
		return driveLink;
	}

	public void setDriveLink(String driveLink) {
		this.driveLink = driveLink;
	}

	public String getEmbedLink() {
		return embedLink;
	}

	public void setEmbedLink(String embedLink) {
		this.embedLink = embedLink;
	}

	public boolean isDriveFile() {
		return isDriveFile;
	}

	public void setDriveFile(boolean isDriveFile) {
		this.isDriveFile = isDriveFile;
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
