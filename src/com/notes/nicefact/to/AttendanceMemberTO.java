package com.notes.nicefact.to;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.notes.nicefact.entity.GroupMember;
import com.notes.nicefact.entity.StudentAttendance;
import com.notes.nicefact.enums.AttendanceStatus;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AttendanceMemberTO {
	String email;

	String name;
	
	long id ;
	
	AttendanceStatus status;
	
	long date;
	
	String comments;

	
	public AttendanceMemberTO() {
		super();
	}

	public AttendanceMemberTO(GroupMember member) {
		super();
		this.email = member.getEmail();
		this.name = member.getName();
		this.id = member.getId();
	}
	
	public AttendanceMemberTO(StudentAttendance sa) {
		super();
		this.email = sa.getEmail();
		this.name = sa.getName();
		this.id = sa.getId();
		this.status = sa.getStatus();
	}
	
	public boolean getIsPresent(){
		return AttendanceStatus.PRESENT.equals(status);
	}
	
	public boolean getIsAbsent(){
		return AttendanceStatus.ABSENT.equals(status);
	}
	
	
	public boolean getIsLeave(){
		return AttendanceStatus.LEAVE.equals(status);
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

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public AttendanceStatus getStatus() {
		return status;
	}

	public void setStatus(AttendanceStatus status) {
		this.status = status;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
	
	
}
