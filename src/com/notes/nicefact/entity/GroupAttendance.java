package com.notes.nicefact.entity;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * @author user
 *
 */

@Entity
public class GroupAttendance extends CommonEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Date attendenceDate;
	
	private String fromTime;
	
	private String toTime;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Group group;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private AppUser teacher;
	
	private String remarks;
	
	@OneToMany(fetch = FetchType.LAZY)
	private List<StudentAttendence> studentAttendences;

	/**
	 * @return the attendenceDate
	 */
	public Date getAttendenceDate() {
		return attendenceDate;
	}

	/**
	 * @param attendenceDate the attendenceDate to set
	 */
	public void setAttendenceDate(Date attendenceDate) {
		this.attendenceDate = attendenceDate;
	}

	/**
	 * @return the fromTime
	 */
	public String getFromTime() {
		return fromTime;
	}

	/**
	 * @param fromTime the fromTime to set
	 */
	public void setFromTime(String fromTime) {
		this.fromTime = fromTime;
	}

	/**
	 * @return the toTime
	 */
	public String getToTime() {
		return toTime;
	}

	/**
	 * @param toTime the toTime to set
	 */
	public void setToTime(String toTime) {
		this.toTime = toTime;
	}

	/**
	 * @return the group
	 */
	public Group getGroup() {
		return group;
	}

	/**
	 * @param group the group to set
	 */
	public void setGroup(Group group) {
		this.group = group;
	}

	/**
	 * @return the teacher
	 */
	public AppUser getTeacher() {
		return teacher;
	}

	/**
	 * @param teacher the teacher to set
	 */
	public void setTeacher(AppUser teacher) {
		this.teacher = teacher;
	}

	/**
	 * @return the remarks
	 */
	public String getRemarks() {
		return remarks;
	}

	/**
	 * @param remarks the remarks to set
	 */
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

}
