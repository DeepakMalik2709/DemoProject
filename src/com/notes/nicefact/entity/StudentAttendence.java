/**
 * 
 */
package com.notes.nicefact.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

/**
 * @author user
 *
 */
@Entity
public class StudentAttendence extends CommonEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public enum ATTENDENCE_STATUS {
		PRESENT, ABSENT, NA;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	private GroupAttendance groupAttendence;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private AppUser student;

	@Enumerated(EnumType.STRING)
	private ATTENDENCE_STATUS status;

	/**
	 * @return the groupAttendence
	 */
	public GroupAttendance getGroupAttendence() {
		return groupAttendence;
	}

	/**
	 * @param groupAttendence the groupAttendence to set
	 */
	public void setGroupAttendence(GroupAttendance groupAttendence) {
		this.groupAttendence = groupAttendence;
	}

	/**
	 * @return the student
	 */
	public AppUser getStudent() {
		return student;
	}

	/**
	 * @param student the student to set
	 */
	public void setStudent(AppUser student) {
		this.student = student;
	}

	/**
	 * @return the status
	 */
	public ATTENDENCE_STATUS getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(ATTENDENCE_STATUS status) {
		this.status = status;
	}
	
}
