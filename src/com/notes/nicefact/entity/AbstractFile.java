package com.notes.nicefact.entity;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

import com.notes.nicefact.enums.FilePermission;
import com.notes.nicefact.to.FileTO;

/**
 * @author JKB
 *	abstract class with main fields for attached files.
 */
@MappedSuperclass
public abstract class AbstractFile  extends CommonEntity{

	private static final long serialVersionUID = 1L;
	
	public enum UPLOAD_TYPE{
		SERVER, GOOGLE_DRIVE
	}
	
	String name;
	
	String serverName;
	
	String mimeType;
	
	String size;
	
	long sizeBytes = 0;
	
	long downloadCount = 0;
	
	String path;
	
	String thumbnail;
	
	String googleDriveId;
	
	String driveLink;
	
	String icon;
	
	String embedLink;
	
	String tempGoogleDriveId;
	
	@Enumerated(EnumType.STRING)
	UPLOAD_TYPE uploadType = UPLOAD_TYPE.SERVER;
	
	//permission on file for recipients
	@Enumerated(EnumType.STRING)
	FilePermission permission = FilePermission.DOWNLOAD;

	public AbstractFile(FileTO file , String path) {
		super();
		this.name = file.getName();
		this.serverName = file.getServerName();
		this.mimeType = file.getMimeType();
		this.size = file.getSize();
		this.sizeBytes = file.getSizeBytes();
		this.path = path;
	}

	public AbstractFile() {
		super();
		// TODO Auto-generated constructor stub
	}


	public String getDriveLink() {
		return driveLink;
	}

	public void setDriveLink(String driveLink) {
		this.driveLink = driveLink;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getEmbedLink() {
		return embedLink;
	}

	public void setEmbedLink(String embedLink) {
		this.embedLink = embedLink;
	}

	public String getGoogleDriveId() {
		return googleDriveId;
	}

	public void setGoogleDriveId(String googleDriveId) {
		this.googleDriveId = googleDriveId;
	}

	public UPLOAD_TYPE getUploadType() {
		return uploadType;
	}

	public void setUploadType(UPLOAD_TYPE uploadType) {
		this.uploadType = uploadType;
	}

	public String getTempGoogleDriveId() {
		return tempGoogleDriveId;
	}

	public void setTempGoogleDriveId(String tempGoogleDriveId) {
		this.tempGoogleDriveId = tempGoogleDriveId;
	}

	public String getName() {
		return name;
	}

	public long getDownloadCount() {
		return downloadCount;
	}

	public void setDownloadCount(long downloadCount) {
		this.downloadCount = downloadCount;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public long getSizeBytes() {
		return sizeBytes;
	}

	public void setSizeBytes(long sizeBytes) {
		this.sizeBytes = sizeBytes;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public FilePermission getPermission() {
		return permission;
	}

	public void setPermission(FilePermission permission) {
		this.permission = permission;
	}
	
	public long incrementDownloadCount() {
		this.downloadCount++;
		return this.downloadCount;
	}
}
