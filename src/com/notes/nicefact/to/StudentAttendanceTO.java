/**
 * 
 */
package com.notes.nicefact.to;

import com.notes.nicefact.enums.AttendanceStatus;

/**
 * @author user
 *
 */

public class StudentAttendanceTO  {

	
	private Long studentKey;
	
	private Long groupAttendanceKey;

	private AttendanceStatus status;

	private String createdBy;

	public Long getStudentKey() {
		return studentKey;
	}

	public void setStudentKey(Long studentKey) {
		this.studentKey = studentKey;
	}

	public Long getGroupAttendanceKey() {
		return groupAttendanceKey;
	}

	public void setGroupAttendanceKey(Long groupAttendanceKey) {
		this.groupAttendanceKey = groupAttendanceKey;
	}

	public AttendanceStatus getStatus() {
		return status;
	}

	public void setStatus(AttendanceStatus status) {
		this.status = status;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	
}
