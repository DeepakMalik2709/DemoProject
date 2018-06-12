package com.notes.nicefact.to;

import org.json.JSONException;
import org.json.JSONObject;

public class GoogleDriveParent {

	String id;
	String selfLink;
	String parentLink;
	String kind;
	Boolean isRoot;
	
	public GoogleDriveParent() {
		super();
	}


	public GoogleDriveParent(JSONObject parentObject) {
		super();
		try {
			if (parentObject.has("id")) {
				id = parentObject.getString("id");
			}
			if (parentObject.has("selfLink")) {
				selfLink = parentObject.getString("selfLink");
			}
			if (parentObject.has("parentLink")) {
				parentLink = parentObject.getString("parentLink");
			}
			if (parentObject.has("kind")) {
				kind = parentObject.getString("kind");
			}
			if (parentObject.has("isRoot")) {
				isRoot = parentObject.getBoolean("isRoot");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public String getSelfLink() {
		return selfLink;
	}


	public void setSelfLink(String selfLink) {
		this.selfLink = selfLink;
	}


	public String getParentLink() {
		return parentLink;
	}


	public void setParentLink(String parentLink) {
		this.parentLink = parentLink;
	}


	public String getKind() {
		return kind;
	}


	public void setKind(String kind) {
		this.kind = kind;
	}


	public Boolean getIsRoot() {
		return isRoot;
	}


	public void setIsRoot(Boolean isRoot) {
		this.isRoot = isRoot;
	}


	@Override
	public String toString() {
		return "GoogleDriveParent [id=" + id + ", isRoot=" + isRoot + "]";
	}


}
