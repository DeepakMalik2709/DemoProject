package com.notes.nicefact.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

public class Schedule extends AbstractComment {

	private static final long serialVersionUID = 1L;
	
	@Basic
	private String title;	
	
	@Basic
	private Date fromDate;
	
	@Basic
	private Date toDate;
	
	
	
	@OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL)
	protected List<ScheduleResponse> scheduleResponse = new ArrayList<>();

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	protected List<ScheduleGroup> scheduleGroup = new ArrayList<>();

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	protected List<ScheduleFile> files = new ArrayList<>();
	
	
	private String zipFilePath;
	
	
	@Basic
	Date zipFileDate;
	
	String googleDriveFolderId;

	public String getZipFilePath() {
		return zipFilePath;
	}

	public void setZipFilePath(String zipFilePath) {
		this.zipFilePath = zipFilePath;
	}

	public String getGoogleDriveFolderId() {
		return googleDriveFolderId;
	}

	public void setGoogleDriveFolderId(String googleDriveFolderId) {
		this.googleDriveFolderId = googleDriveFolderId;
	}

	public Date getZipFileDate() {
		return zipFileDate;
	}

	public void setZipFileDate(Date zipFileDate) {
		this.zipFileDate = zipFileDate;
	}

	@PrePersist
	@PreUpdate
	void prePersist() {
		super.preStore();
	
		
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public List<ScheduleResponse> getScheduleResponse() {
		return scheduleResponse;
	}

	public void setScheduleResponse(List<ScheduleResponse> scheduleResponse) {
		this.scheduleResponse = scheduleResponse;
	}

	public List<ScheduleGroup> getScheduleGroup() {
		return scheduleGroup;
	}

	public void setScheduleGroup(List<ScheduleGroup> scheduleGroup) {
		this.scheduleGroup = scheduleGroup;
	}

	public List<ScheduleFile> getFiles() {
		return files;
	}

	public void setFiles(List<ScheduleFile> files) {
		this.files = files;
	}

}
