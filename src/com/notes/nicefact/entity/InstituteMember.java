package com.notes.nicefact.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import com.notes.nicefact.enums.UserPosition;

/**
 *  This will be the top most group an organisation can have, or we can call it home page of XYZ institute.
 *  
 * @author jkb
 *
 */

@Entity
public class InstituteMember extends CommonEntity {

	private static final long serialVersionUID = 1L;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Institute institute;
	
	@Enumerated(EnumType.STRING)
	@ElementCollection(fetch = FetchType.EAGER  )
	private Set<UserPosition> positions = new HashSet<>();
	
	String email;

	String name;

	Boolean isBlocked = true;
	
	Boolean isAppUser = true;
	
	Boolean isJoinRequestApproved = false;

	String joinRequestApprover ;
	
	@Basic
	Date  joinRequestApproveDate;
	String department;
	
	String organization;
	
	boolean isNotificationSent = false;

	public Institute getInstitute() {
		return institute;
	}

	public void setInstitute(Institute institute) {
		this.institute = institute;
	}

	public Set<UserPosition> getPositions() {
		if(null == positions){
			positions = new HashSet<>();
		}
		return positions;
	}

	public void setPositions(Set<UserPosition> positions) {
		this.positions = positions;
	}

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

	public Boolean getIsAdmin() {
		return getPositions().contains(UserPosition.ADMIN);
	}

	public Boolean getIsBlocked() {
		return isBlocked;
	}

	public void setIsBlocked(Boolean isBlocked) {
		this.isBlocked = isBlocked;
	}

	public Boolean getIsAppUser() {
		return isAppUser;
	}

	public void setIsAppUser(Boolean isAppUser) {
		this.isAppUser = isAppUser;
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

	public boolean getIsNotificationSent() {
		return isNotificationSent;
	}

	public void setIsNotificationSent(boolean isNotificationSent) {
		this.isNotificationSent = isNotificationSent;
	}
	
	public Boolean getIsJoinRequestApproved() {
		if(null == isJoinRequestApproved){
			isJoinRequestApproved = true;
		}
		return isJoinRequestApproved;
	}

	public void setIsJoinRequestApproved(Boolean isJoinRequestApproved) {
		this.isJoinRequestApproved = isJoinRequestApproved;
	}

	public String getJoinRequestApprover() {
		return joinRequestApprover;
	}

	public void setJoinRequestApprover(String joinRequestApprover) {
		this.joinRequestApprover = joinRequestApprover;
	}

	public Date getJoinRequestApproveDate() {
		return joinRequestApproveDate;
	}

	public void setJoinRequestApproveDate(Date joinRequestApproveDate) {
		this.joinRequestApproveDate = joinRequestApproveDate;
	}

	public InstituteMember(String email, String name) {
		super();
		this.email = email;
		this.name = name;
		this.isAppUser = false;
	}

	public  InstituteMember(AppUser appuser) {
		super();
		this.email = appuser.getEmail();
		this.name = appuser.getDisplayName();
		this.department = appuser.getDepartment();
		this.organization = appuser.getOrganization();
		this.isAppUser = true;
	}
	
	
	public InstituteMember() {
		super();
	}

}
