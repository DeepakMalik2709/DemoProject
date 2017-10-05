package com.notes.nicefact.to;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.notes.nicefact.entity.GroupMember;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupMemberTO {

	String email;

	String name;
	
	boolean isAdmin;
	
	boolean isBlocked;
	
	String position;
	
	String department;
	
	String organization;
	
	long id ;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
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

	public void setBlocked(boolean isBlocked) {
		this.isBlocked = isBlocked;
	}

	public boolean getIsBlocked() {
		return isBlocked;
	}

	public void setIsBlocked(boolean isBlocked) {
		this.isBlocked = isBlocked;
	}
	public GroupMemberTO() {
		super();
	}

	public GroupMemberTO(GroupMember member) {
		super();
		this.email = member.getEmail();
		this.name = member.getName();
		this.isAdmin = member.getIsAdmin();
		this.position = member.getPosition();
		this.department = member.getDepartment();
		this.organization = member.getOrganization();
		this.isBlocked = member.getIsBlocked();
		this.id = member.getId();
	}

	public GroupMemberTO(String email, String name, boolean isAdmin, boolean isBlocked, String position, String department, String organization, long id) {
		super();
		this.email = email;
		this.name = name;
		this.isAdmin = isAdmin;
		this.isBlocked = isBlocked;
		this.position = position;
		this.department = department;
		this.organization = organization;
		this.id = id;
	}

	public GroupMemberTO(String email) {
		super();
		this.email = email;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean getIsAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
	
	
}
