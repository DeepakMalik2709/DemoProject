package com.notes.nicefact.to;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.api.services.calendar.model.EventAttachment;
import com.notes.nicefact.entity.AbstractFile;
import com.notes.nicefact.util.Utils;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FileTO {

	String name;
	
	String serverName;
	
	String mimeType;
	
	String size;
	
	String icon;
	
	long sizeBytes;
	
	long downloadCount = 0;
	boolean hasThumbnail ; 
	String thumbnailLink;
	long id;
	
	String driveLink;
	
	String embedLink;
	boolean isDriveFile;
	
	public FileTO(EventAttachment attach) {
		super();
		this.name = attach.getTitle();
		this.serverName = attach.getFileUrl();
		this.mimeType = attach.getMimeType();
		this.sizeBytes = attach.size();
		this.size = Utils.readableFileSize(sizeBytes);
		// TODO Auto-generated constructor stub
	}

	public FileTO(String name, String serverName, String mimeType,  long sizeBytes) {
		super();
		this.name = name;
		this.serverName = serverName;
		this.mimeType = mimeType;
		this.sizeBytes = sizeBytes;
		this.size = Utils.readableFileSize(sizeBytes);
	}

	public FileTO(AbstractFile file) {
		super();
		this.name = file.getName();
		this.serverName = file.getServerName();
		this.mimeType = file.getMimeType();
		this.size = file.getSize();
		this.sizeBytes = file.getSizeBytes();
		this.downloadCount = file.getDownloadCount();
		this.id = file.getId();
		this.hasThumbnail = StringUtils.isNotBlank(file.getThumbnail());
		this.thumbnailLink = submission.getThumbnail();
		this.icon = file.getIcon();
		this.embedLink = file.getEmbedLink();
		this.driveLink = file.getDriveLink();
		isDriveFile = StringUtils.isNotBlank(this.driveLink);
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

	public boolean getIsDriveFile() {
		return isDriveFile;
	}

	public String getThumbnailLink() {
		return thumbnailLink;
	}

	public void setThumbnailLink(String thumbnailLink) {
		this.thumbnailLink = thumbnailLink;
	}

	public void setIsDriveFile(boolean isDriveFile) {
		this.isDriveFile = isDriveFile;
	}

	public boolean getHasThumbnail() {
		return hasThumbnail;
	}

	public void setHasThumbnail(boolean hasThumbnail) {
		this.hasThumbnail = hasThumbnail;
	}

	public long getDownloadCount() {
		return downloadCount;
	}

	public void setDownloadCount(long downloadCount) {
		this.downloadCount = downloadCount;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
}
