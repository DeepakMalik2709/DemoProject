package com.notes.nicefact.to;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.notes.nicefact.entity.GroupMember;
import com.notes.nicefact.entity.StudentAttendance;
import com.notes.nicefact.enums.AttendanceStatus;
import com.notes.nicefact.util.DateUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AttendanceMemberTO {
	String email;

	String name;
	
	long id ;
	
	AttendanceStatus status;
	
	long date;
	
	String comments;
	
	String dateString;
	
	String toTimeString;
	
	String fromTimeString;
		
	
	
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
		this.date =  sa.getDate().getTime();
		this.dateString = DateUtils.formatDate(sa.getDate(), DateUtils.DEFAULT_PATTERN);
		this.toTimeString = sa.getGroupAttendance().getToTime();
		this.fromTimeString = sa.getGroupAttendance().getFromTime();
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

	public String getDateString() {
		return dateString;
	}

	public void setDateString(String dateString) {
		this.dateString = dateString;
	}

	public String getToTimeString() {
		return toTimeString;
	}

	public void setToTimeString(String toTimeString) {
		this.toTimeString = toTimeString;
	}

	public String getFromTimeString() {
		return fromTimeString;
	}

	public void setFromTimeString(String fromTimeString) {
		this.fromTimeString = fromTimeString;
	}
	
	
}
