package com.notes.nicefact.to;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Jitender
 * 
 *         POJO to hold a single permission for google drive files
 * 
 */
public class GoogleFilePermission implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String id;

	String fileId;

	String name;

	String role;

	String photoLink;

	String type;

	String value;

	String withLink;
	
	String emailAddress;
	
	private String domain;
	
	boolean isPermissionUpdated = false;
	
	private String selfLink;

	public JSONObject getJson() {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("id", id);
			jsonObject.put("name", name);
			jsonObject.put("role", role);
			if (!StringUtils.isBlank(photoLink)) {
				jsonObject.put("photoLink", photoLink);
			}
			if (!StringUtils.isBlank(type)) {
				jsonObject.put("type", type);
			}
			if (!StringUtils.isBlank(fileId)) {
				jsonObject.put("fileId", fileId);
			}
			if (!StringUtils.isBlank(value)) {
				jsonObject.put("value", value);
			}
			if (!StringUtils.isBlank(withLink)) {
				jsonObject.put("withLink",withLink);
			}
			if (!StringUtils.isBlank(emailAddress)) {
				jsonObject.put("email", emailAddress);
			}
			if (!StringUtils.isBlank(domain)) {
				jsonObject.put("domain", domain);
			}
			if (!StringUtils.isBlank(selfLink)) {
				jsonObject.put("selfLink", selfLink);
			}
			
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonObject;
	}

	
/*
	@Override
	public String toString() {
		return "GoogleFilePermission [id=" + id + ", fileId=" + fileId
				+ ", name=" + name + ", role=" + role + ", photoLink="
				+ photoLink + ", type=" + type + ", value=" + value
				+ ", withLink=" + withLink + ", emailAddress=" + emailAddress
				+ ", isPermissionUpdated=" + isPermissionUpdated + "]";
	}
*/


	public JSONObject getGoogleDriveJson() {
		JSONObject jsonObject = new JSONObject();
		try {
			if (!StringUtils.isBlank(id)) {
			jsonObject.put("id", id);
			}
			jsonObject.put("kind", "drive#permission");
			if (!StringUtils.isBlank(name)) {
			jsonObject.put("name", name);
			}
			if (!StringUtils.isBlank(role)) {
			jsonObject.put("role", role);
			}
			if (!StringUtils.isBlank(photoLink)) {
				jsonObject.put("photoLink", photoLink);
			}
			if (!StringUtils.isBlank(type)) {
				jsonObject.put("type", type);
			}
			if (!StringUtils.isBlank(value)) {
				jsonObject.put("value", value);
			}
			if (!StringUtils.isBlank(withLink)) {
				jsonObject.put("withLink", withLink);
			}
			if (!StringUtils.isBlank(emailAddress)) {
				jsonObject.put("emailAddress", emailAddress);
			}
			if (!StringUtils.isBlank(fileId)) {
				jsonObject.put("fileId", fileId);
			}
			if (!StringUtils.isBlank(domain)) {
				jsonObject.put("domain", domain);
			}
			if (!StringUtils.isBlank(selfLink)) {
				jsonObject.put("selfLink", selfLink);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonObject;
	}

	public String getPhotoLink() {
		return photoLink;
	}

	public boolean getIsPermissionUpdated() {
		return isPermissionUpdated;
	}

	public String getWithLink() {
		return withLink;
	}

	public void setWithLink(String withLink) {
		this.withLink = withLink;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setIsPermissionUpdated(boolean isPermissionUpdated) {
		this.isPermissionUpdated = isPermissionUpdated;
	}

	public void setPhotoLink(String photoLink) {
		this.photoLink = photoLink;
	}

	public String getType() {
		return type;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public GoogleFilePermission(String id, String value, String role,
			String type) {
		super();
		this.id = id;
		this.value = value;
		this.role = role;
		this.type = type;
	}
	
	public GoogleFilePermission(String id, String value, String role,
			String type, boolean withLink) {
		super();
		this.id = id;
		this.value = value;
		this.role = role;
		this.type = type;
		this.withLink = withLink + "";
	}

	public GoogleFilePermission(JSONObject jsonObject) {
		try {
			if (jsonObject.has("id")) {
				id = jsonObject.getString("id");
			}
			if (jsonObject.has("name")) {
				name = jsonObject.getString("name");
			}
			if (jsonObject.has("additionalRoles")) {
				JSONArray additionalRoles = jsonObject.getJSONArray("additionalRoles");
				for(int i = 0 ;i<additionalRoles.length();i++){
					if(additionalRoles.getString(i).equalsIgnoreCase("commenter")){
						role = additionalRoles.getString(i);
						break;
					}
				}
			} else if (jsonObject.has("role")) {
				role = jsonObject.getString("role");
			}
			if (jsonObject.has("photoLink")) {
				photoLink = jsonObject.getString("photoLink");
			}

			if (jsonObject.has("type")) {
				type = jsonObject.getString("type");
			}
			if (jsonObject.has("fileId")) {
				fileId = jsonObject.getString("fileId");
			}

			if (jsonObject.has("value")) {
				value = jsonObject.getString("value");
			}
			if (jsonObject.has("withLink")) {
				withLink = jsonObject.get("withLink") + "";
			}
			if (jsonObject.has("emailAddress")) {
				emailAddress = jsonObject.getString("emailAddress");
			}
			if (jsonObject.has("selfLink")) {
				selfLink = jsonObject.getString("selfLink");
			}
			if (jsonObject.has("domain")) {
				domain = jsonObject.getString("domain");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	

	/**
	 * @return the domain
	 */
	public String getDomain() {
		return domain;
	}


	/**
	 * @param domain the domain to set
	 */
	public void setDomain(String domain) {
		this.domain = domain;
	}


	/**
	 * @return the selfLink
	 */
	public String getSelfLink() {
		return selfLink;
	}


	/**
	 * @param selfLink the selfLink to set
	 */
	public void setSelfLink(String selfLink) {
		this.selfLink = selfLink;
	}


	public GoogleFilePermission() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GoogleFilePermission other = (GoogleFilePermission) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "GoogleFilePermission [id=" + id + ", fileId=" + fileId
				+ ", name=" + name + ", role=" + role + ", photoLink="
				+ photoLink + ", type=" + type + ", value=" + value
				+ ", withLink=" + withLink + ", emailAddress=" + emailAddress
				+ ", isPermissionUpdated=" + isPermissionUpdated + "]";
	}

}
