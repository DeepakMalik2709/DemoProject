/**
 * 
 */
package com.notes.nicefact.entity;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import com.notes.nicefact.enums.AttendanceStatus;
import com.notes.nicefact.to.AttendanceMemberTO;

/**
 * @author user
 *
 */
@Entity
public class StudentAttendance extends CommonEntity {
	private static final long serialVersionUID = 1L;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private GroupAttendance groupAttendance;
	
	String email;

	String name;
	
	String comments;
	
	@Enumerated(EnumType.STRING)
	private AttendanceStatus status;
	
	long groupId;
	
	@Basic
	Date date;

	public StudentAttendance(){
		
	}
	
	public StudentAttendance(AttendanceMemberTO member , GroupAttendance groupAttendance) {
		this.groupAttendance = groupAttendance;
		this.status = member.getStatus();
		this.email = member.getEmail();
		this.name = member.getName();
		this.comments = member.getComments();
		this.groupId = groupAttendance.getGroup().getId();
		this.date = groupAttendance.getDate();
	}



	public GroupAttendance getGroupAttendance() {
		return groupAttendance;
	}

	public void setGroupAttendance(GroupAttendance groupAttendance) {
		this.groupAttendance = groupAttendance;
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

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public long getGroupId() {
		return groupId;
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the status
	 */
	public AttendanceStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(AttendanceStatus status) {
		this.status = status;
	}
	
}
