package com.notes.nicefact.to;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.notes.nicefact.entity.GroupMember;
import com.notes.nicefact.entity.InstituteMember;
import com.notes.nicefact.enums.UserPosition;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupMemberTO {

	String email;

	String name;
	
	boolean isAdmin;
	
	boolean isBlocked;
	
	boolean isJoinRequestApproved ;
	
	String department;
	
	String organization;
	
	private Set<UserPosition> positions = new HashSet<>();
	
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
		this.department = member.getDepartment();
		this.organization = member.getOrganization();
		this.isBlocked = member.getIsBlocked();
		this.id = member.getId();
		
	}

	public GroupMemberTO(InstituteMember member) {
		super();
		this.email = member.getEmail();
		this.name = member.getName();
		this.isAdmin = member.getIsAdmin();
		this.department = member.getDepartment();
		this.organization = member.getOrganization();
		this.isBlocked = member.getIsBlocked();
		this.id = member.getId();
		this.positions = member.getPositions();
		this.isJoinRequestApproved = member.getIsJoinRequestApproved();
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

	public Set<UserPosition> getPositions() {
		return positions;
	}

	public void setPositions(Set<UserPosition> positions) {
		this.positions = positions;
	}
	
	
}
