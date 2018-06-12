/**
 * 
 */
package com.notes.nicefact.to;

import java.util.Date;

import com.notes.nicefact.entity.StudentAttendance;


public class StudentAttendanceTO  {
	
	String email;

	String name;
	
	String comments;
	 
	private String status;
	
	long groupId;
	
	Date date;
	
	public StudentAttendanceTO(StudentAttendance studentAttendance){
			this.email=studentAttendance.getEmail();
			this.name = studentAttendance.getName();
			this.status =  studentAttendance.getStatus().toString();
			this.groupId = studentAttendance.getGroupId();
			this.date = studentAttendance.getDate();
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
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String 	status) {
		this.status = status;
	}
	
}