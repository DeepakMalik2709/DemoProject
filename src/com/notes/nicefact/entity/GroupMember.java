package com.notes.nicefact.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import com.notes.nicefact.to.AppUserTO;

/**
 * @author JKB
 * Group and members 
 */
@Entity
public class GroupMember extends CommonEntity{
	private static final long serialVersionUID = 1L;

	// Ancestor
	@ManyToOne(fetch = FetchType.LAZY)
	private Group group;

	// email of group member
	String email;

	// name of group member
	String name;

	Boolean isAdmin = false;

	Boolean isBlocked = false;
	
	Boolean isAppUser = true;

	private String position;
	
	private String folderPermissionId;
	
	private String error;
	
	String department;
	
	String organization;
	
	boolean isNotificationSent = false;

	public GroupMember(String email, String name) {
		super();
		this.email = email;
		this.name = name;
	}

	public  GroupMember(AppUser appuser) {
		super();
		this.email = appuser.getEmail();
		this.name = appuser.getDisplayName();
		this.position = appuser.getPosition();
		this.department = appuser.getDepartment();
		this.organization = appuser.getOrganization();
	}
	
	public  GroupMember(AppUserTO appuser) {
		super();
		this.email = appuser.getEmail();
		this.name = appuser.getDisplayName();
		this.position = appuser.getPosition();
		this.department = appuser.getDepartment();
		this.organization = appuser.getOrganization();
	}
	
	public GroupMember() {
		super();
	}


	public boolean getIsNotificationSent() {
		return isNotificationSent;
	}

	public void setIsNotificationSent(boolean isNotificationSent) {
		this.isNotificationSent = isNotificationSent;
	}

	public Boolean getIsAppUser() {
		return isAppUser;
	}

	public void setIsAppUser(Boolean isAppUser) {
		this.isAppUser = isAppUser;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}


	public Boolean getIsAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(Boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public Boolean getIsBlocked() {
		if(null == isBlocked){
			isBlocked = false;
		}
		return isBlocked;
	}

	public void setIsBlocked(Boolean isBlocked) {
		this.isBlocked = isBlocked;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getFolderPermissionId() {
		return folderPermissionId;
	}

	public void setFolderPermissionId(String folderPermissionId) {
		this.folderPermissionId = folderPermissionId;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	@Override
	public String toString() {
		return "GroupMember [email=" + email + ", name=" + name + "]";
	}

	@PreUpdate
	@PrePersist
	void prePersist() {
		super.preStore();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		return result;
	}

	
	/* we are saving members as a set , so we need email for equality */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GroupMember other = (GroupMember) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		return true;
	}
}