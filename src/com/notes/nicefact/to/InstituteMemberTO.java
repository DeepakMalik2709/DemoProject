package com.notes.nicefact.to;

import java.util.Date;

import javax.persistence.Basic;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.notes.nicefact.entity.InstituteMember;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InstituteMemberTO {
	
	Long id;
	
	String email;

	String name;

	Boolean isBlocked = true;
	
	Boolean isAppUser = true;
	
	Boolean isJoinRequestApproved = false;

	String joinRequestApprover ;
	
	Date  joinRequestApproveDate;
	String department;
	
	String organization;
	
	boolean isNotificationSent = false;
	
	String uniqueId;
	
	String rollNo;
	
	InstituteTO institute = new InstituteTO();
	
	public InstituteMemberTO() {
		
	}
	

	public InstituteMemberTO(InstituteMember instMember ) {
		this.id = instMember.getId();
		this.email = instMember.getEmail();
		this.name = instMember.getName();
		this.isBlocked =instMember.getIsBlocked();
		this.isAppUser = instMember.getIsAppUser();
		this.isJoinRequestApproved = instMember.getIsJoinRequestApproved();
		
		this.department = instMember.getDepartment();
		if(instMember.getOrganization() != null){
			this.organization = instMember.getOrganization();
		}else{
			this.setInstitute(new InstituteTO(instMember.getInstitute()));
			this.organization = this.getInstitute().getName();
		}
		this.uniqueId =instMember.getUniqueId();
		this.rollNo = instMember.getRollNo();
	}

	
	
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public InstituteTO getInstitute() {
		return institute;
	}


	public void setInstitute(InstituteTO institute) {
		this.institute = institute;
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


	public Boolean getIsJoinRequestApproved() {
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


	public boolean isNotificationSent() {
		return isNotificationSent;
	}


	public void setNotificationSent(boolean isNotificationSent) {
		this.isNotificationSent = isNotificationSent;
	}


	public String getUniqueId() {
		return uniqueId;
	}


	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}


	public String getRollNo() {
		return rollNo;
	}


	public void setRollNo(String rollNo) {
		this.rollNo = rollNo;
	}

	

}
