package com.notes.nicefact.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.notes.nicefact.exception.AppException;
import com.notes.nicefact.to.GroupAttendanceTO;
import com.notes.nicefact.util.CurrentContext;

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
	
	@Basic
	Date date;
	
	private String fromTime;
	
	private String toTime;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Group group;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private AppUser teacher;
	
	private String comments;
	
	private String teacherEmail;
	
	@OneToMany(mappedBy = "groupAttendance", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval=true)
	private List<StudentAttendance> studentAttendances;
	
	public GroupAttendance() {}
	
	public GroupAttendance(GroupAttendanceTO groupAttendanceTO) {
		super();
		this.updateProps(groupAttendanceTO);
	}
	
	public void updateProps(GroupAttendanceTO groupAttendanceTO) {
		this.teacher = CurrentContext.getAppUser();
		if(this.teacher==null){
			throw new AppException("Teacher cannot be null");
		}
		this.date = new Date(groupAttendanceTO.getDate());
		this.comments =  groupAttendanceTO.getComments();
		this.fromTime = groupAttendanceTO.getFromTime();
		this.toTime = groupAttendanceTO.getToTime();
		this.teacherEmail = this.teacher.getEmail();
		
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getTeacherEmail() {
		return teacherEmail;
	}

	public void setTeacherEmail(String teacherEmail) {
		this.teacherEmail = teacherEmail;
	}



	public List<StudentAttendance> getStudentAttendances() {
		if(null == studentAttendances){
			studentAttendances = new ArrayList<>();
		}
		return studentAttendances;
	}

	public void setStudentAttendances(List<StudentAttendance> studentAttendances) {
		this.studentAttendances = studentAttendances;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
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



}
