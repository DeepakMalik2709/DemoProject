package com.notes.nicefact.to;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.notes.nicefact.util.Utils;

import flexjson.JSONSerializer;

public class GoogleDriveFile {

	private static final Logger logger = Logger.getLogger(GoogleDriveFile.class.getName());



	String id;
	String webContentLink;
	String editLink;
	String selfLink;
	String embedLink;
	String title;
	String mimeType;
	String createdDate;
	String modifiedDate;
	long lastModifiedMiliSeconds;
	String iconLink;
	String lastModifyingUserName;
	String thumbnailLink;
	String lastModifyingUserPicture;
	String description;
	String author;
	String authorPicture;
	String authorEmail;
	String pdfExportLink;
	String authorPermissionId;
	String processingState;
	boolean isStarred = false;
	boolean isTrashed = false;
	boolean shared;
	String fileSize;
	List<GoogleDriveParent> parents = new ArrayList<>();
	Map<String, String> exportLinks = new HashMap<>();
	String modifiedByMeDate;
	String lastViewedByMeDate;
	String userPermission;
	String currentUser;

	String serverPath ; 

	// downloadUrl cannot be found for all files
	String downloadUrl;

	private Long revisions;

	public GoogleDriveFile() {
		super();
	}

	/**
	 * This function identify current revision of file on head.
	 * 
	 * @return
	 */
	private Long identifyCurrentRevision() {

		try {
			revisions = Long.valueOf(id);
		} catch (Exception e) {

		}
		return revisions;
	}

	public GoogleDriveFile(JSONObject fileObject) {
		super();
		try {
			if (fileObject.has("id")) {
				id = fileObject.getString("id");
				identifyCurrentRevision();
			}
			if (fileObject.has("selfLink")) {
				selfLink = fileObject.getString("selfLink");
			}
			if (fileObject.has("webContentLink")) {
				webContentLink = fileObject.getString("webContentLink");
			} else {
				webContentLink = selfLink;
			}

			if (fileObject.has("alternateLink")) {
				editLink = fileObject.getString("alternateLink");
			}
			if (fileObject.has("embedLink")) {
				embedLink = fileObject.getString("embedLink");
			} else {
				embedLink = "https://docs.google.com/viewer?srcid=" + id + "&pid=explorer&efh=false&a=v&chrome=false&embedded=true";
			}

			if (fileObject.has("title")) {
				title = fileObject.getString("title");
			}
			if (fileObject.has("mimeType")) {
				mimeType = fileObject.getString("mimeType");
			}
			if (fileObject.has("createdDate")) {
				createdDate = fileObject.getString("createdDate");
			}
			if (fileObject.has("modifiedDate")) {
				modifiedDate = fileObject.getString("modifiedDate");
				Date lastModifiedDate = Utils.getModifiedDate(modifiedDate, 0);
				lastModifiedMiliSeconds = lastModifiedDate.getTime();
			}
			if (fileObject.has("iconLink")) {
				iconLink = fileObject.getString("iconLink");
			}
			if (fileObject.has("lastModifyingUserName")) {
				lastModifyingUserName = fileObject.getString("lastModifyingUserName");
				lastModifyingUserName = Utils.cutStr(lastModifyingUserName, 14);
			}
			if (fileObject.has("lastModifyingUser")) {
				JSONObject lastModifyingUserJSON = fileObject.getJSONObject("lastModifyingUser");
				if (lastModifyingUserJSON.has("picture") && lastModifyingUserJSON.getJSONObject("picture").has("url")) {
					lastModifyingUserPicture = lastModifyingUserJSON.getJSONObject("picture").getString("url");
				}
			}
			if (fileObject.has("thumbnailLink")) {
				thumbnailLink = fileObject.getString("thumbnailLink");
			}
			if (StringUtils.isNotBlank(thumbnailLink)) {
				thumbnailLink = thumbnailLink.replace("s220", "s600" );
			}

			if (fileObject.has("description")) {
				description = fileObject.getString("description");
			}
			if (fileObject.has("owners") && fileObject.getJSONArray("owners").length() > 0) {
				JSONObject ownerJson = fileObject.getJSONArray("owners").getJSONObject(0);
				author = ownerJson.getString("displayName");
				author = Utils.cutStr(author, 14);
				if (ownerJson.has("picture") && ownerJson.getJSONObject("picture").has("url")) {
					authorPicture = ownerJson.getJSONObject("picture").getString("url");
				}
				authorPermissionId = ownerJson.getString("permissionId");
				if (ownerJson.has("emailAddress")) {
					authorEmail = ownerJson.getString("emailAddress");
				}
			}
			if (fileObject.has("labels")) {
				if (fileObject.getJSONObject("labels").has("starred")) {
					isStarred = fileObject.getJSONObject("labels").getBoolean("starred");
				}
				if (fileObject.getJSONObject("labels").has("trashed")) {
					isTrashed = fileObject.getJSONObject("labels").getBoolean("trashed");
				}
			}
			if (fileObject.has("modifiedByMeDate")) {
				modifiedByMeDate = fileObject.getString("modifiedByMeDate");
			}
			if (fileObject.has("lastViewedByMeDate")) {
				lastViewedByMeDate = fileObject.getString("lastViewedByMeDate");
			}
			if (fileObject.has("fileSize")) {
				fileSize = fileObject.getString("fileSize");
			}
			if (fileObject.has("downloadUrl")) {
				downloadUrl = fileObject.getString("downloadUrl");
			}
			if (fileObject.has("exportLinks")) {
				JSONObject exportLInksJson = fileObject.getJSONObject("exportLinks");
				if (exportLInksJson.has("application/pdf")) {
					pdfExportLink = fileObject.getJSONObject("exportLinks").getString("application/pdf");
				} else if (fileObject.has("downloadUrl") && "application/pdf".equals(fileObject.getString("mimeType"))) {
					pdfExportLink = fileObject.getString("downloadUrl");
				}
				Iterator<String> keysIter = exportLInksJson.keys();
				while (keysIter.hasNext()) {
					String key = keysIter.next();
					exportLinks.put(key, exportLInksJson.getString(key));
				}
			}

			if (fileObject.has("userPermission")) {
				JSONObject userPermissionJSON = fileObject.getJSONObject("userPermission");
				if (userPermissionJSON.has("role")) {
					userPermission = userPermissionJSON.getString("role");
				}
				if (userPermissionJSON.has("name")) {
					currentUser = userPermissionJSON.getString("name");
				}
			}

			if (fileObject.has("parents")) {
				JSONArray parentsArray = fileObject.getJSONArray("parents");
				for (int i = 0; i < parentsArray.length(); i++) {
					JSONObject parentsJSON = parentsArray.getJSONObject(i);
					GoogleDriveParent parent = new GoogleDriveParent(parentsJSON);
					parents.add(parent);
				}
			}

			shared = fileObject.optBoolean("shared");

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}


	public boolean isShared() {
		return shared;
	}

	public void setShared(boolean shared) {
		this.shared = shared;
	}

	public String getModifiedByMeDate() {
		return modifiedByMeDate;
	}

	public void setModifiedByMeDate(String modifiedByMeDate) {
		this.modifiedByMeDate = modifiedByMeDate;
	}

	public String getLastViewedByMeDate() {
		return lastViewedByMeDate;
	}


	public List<GoogleDriveParent> getParents() {
		return parents;
	}

	public String getServerPath() {
		return serverPath;
	}

	public void setServerPath(String serverPath) {
		this.serverPath = serverPath;
	}

	public void setLastViewedByMeDate(String lastViewedByMeDate) {
		this.lastViewedByMeDate = lastViewedByMeDate;
	}

	public String getDescription() {
		return description;
	}

	public String getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(String currentUser) {
		this.currentUser = currentUser;
	}

	public String getEmbedLink() {
		return embedLink;
	}

	public String getLastModifyingUserPicture() {
		return lastModifyingUserPicture;
	}

	public void setLastModifyingUserPicture(String lastModifyingUserPicture) {
		this.lastModifyingUserPicture = lastModifyingUserPicture;
	}

	public String getAuthorPicture() {
		return authorPicture;
	}

	public void setAuthorPicture(String authorPicture) {
		this.authorPicture = authorPicture;
	}

	public String getSelfLink() {
		return selfLink;
	}

	public void setSelfLink(String selfLink) {
		this.selfLink = selfLink;
	}

	public String getIconLink() {
		return iconLink;
	}

	public void setIconLink(String iconLink) {
		this.iconLink = iconLink;
	}

	public String getLastModifyingUserName() {
		return lastModifyingUserName;
	}

	public void setLastModifyingUserName(String lastModifyingUserName) {
		this.lastModifyingUserName = lastModifyingUserName;
	}

	public String getThumbnailLink() {
		return thumbnailLink;
	}

	public void setThumbnailLink(String thumbnailLink) {
		this.thumbnailLink = thumbnailLink;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public boolean isStarred() {
		return isStarred;
	}

	public void setStarred(boolean isStarred) {
		this.isStarred = isStarred;
	}

	public String getUserPermission() {
		return userPermission;
	}

	public void setUserPermission(String userPermission) {
		this.userPermission = userPermission;
	}

	public void setEmbedLink(String embedLink) {
		this.embedLink = embedLink;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getWebContentLink() {
		return webContentLink;
	}

	public void setWebContentLink(String webContentLink) {
		this.webContentLink = webContentLink;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public String getEditLink() {
		return editLink;
	}

	public void setEditLink(String editLink) {
		this.editLink = editLink;
	}

	public boolean isTrashed() {
		return isTrashed;
	}

	public void setTrashed(boolean isTrashed) {
		this.isTrashed = isTrashed;
	}

	public String toJson() {
		return new JSONSerializer().serialize(this);
	}

	public void setParents(List<GoogleDriveParent> parents) {
		this.parents = parents;
	}

	public void setExportLinks(Map<String, String> exportLinks) {
		this.exportLinks = exportLinks;
	}


	public JSONObject toFolderJson() {
		JSONObject json = new JSONObject();
		try {
			json.put("id", id);
			json.put("title", title);
			json.put("icon", iconLink);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return json;
	}

	public String getAuthorPermissionId() {
		return authorPermissionId;
	}

	public void setAuthorPermissionId(String authorPermissionId) {
		this.authorPermissionId = authorPermissionId;
	}



	/**
	 * @return the collavateURL
	 */

	public String getPdfExportLink() {
		return pdfExportLink;
	}

	public void setPdfExportLink(String pdfExportLink) {
		this.pdfExportLink = pdfExportLink;
	}

	public String getAuthorEmail() {
		return authorEmail;
	}

	public void setAuthorEmail(String authorEmail) {
		this.authorEmail = authorEmail;
	}



	public Map<String, String> getExportLinks() {
		return exportLinks;
	}

	public String getProcessingState() {
		return processingState;
	}

	public void setProcessingState(String processingState) {
		this.processingState = processingState;
	}

	public Long getRevisions() {
		return revisions;
	}

	public void setRevisions(Long revisions) {
		this.revisions = revisions;
	}

	public long getLastModifiedMiliSeconds() {
		return lastModifiedMiliSeconds;
	}

	public void setLastModifiedMiliSeconds(long lastModifiedMiliSeconds) {
		this.lastModifiedMiliSeconds = lastModifiedMiliSeconds;
	}

	@Override
	public String toString() {
		return "GoogleDriveFile [id=" + id + ", webContentLink=" + webContentLink + ", editLink=" + editLink + ", selfLink=" + selfLink + ", embedLink=" + embedLink + ", title=" + title
				+ ", mimeType=" + mimeType + ", createdDate=" + createdDate + ", modifiedDate=" + modifiedDate + ", lastModifiedMiliSeconds=" + lastModifiedMiliSeconds + ", iconLink=" + iconLink
				+ ", lastModifyingUserName=" + lastModifyingUserName + ", thumbnailLink=" + thumbnailLink + ", lastModifyingUserPicture=" + lastModifyingUserPicture + ", description=" + description
				+ ", author=" + author + ", authorPicture=" + authorPicture + ", authorEmail=" + authorEmail + ", pdfExportLink=" + pdfExportLink + ", authorPermissionId=" + authorPermissionId
				+ ", processingState=" + processingState + ", isStarred=" + isStarred + ", isTrashed=" + isTrashed + ", shared=" + shared + ", fileSize=" + fileSize + ", parents=" + parents
				+ ", exportLinks=" + exportLinks + ", modifiedByMeDate=" + modifiedByMeDate + ", lastViewedByMeDate=" + lastViewedByMeDate + ", userPermission=" + userPermission + ", currentUser="
				+ currentUser + ", serverPath=" + serverPath + ", downloadUrl=" + downloadUrl + ", revisions=" + revisions + "]";
	}

}
