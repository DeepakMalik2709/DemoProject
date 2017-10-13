package com.notes.nicefact.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.api.services.drive.model.File;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Book {

	private String fileId;
	private File content;
	private String folderId;
	public String getFileId() {
		return fileId;
	}
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	public File getContent() {
		return content;
	}
	public void setContent(File content) {
		this.content = content;
	}
	public String getFolderId() {
		return folderId;
	}
	public void setFolderId(String folderId) {
		this.folderId = folderId;
	}

}
