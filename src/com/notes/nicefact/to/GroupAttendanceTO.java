package com.notes.nicefact.to;

import java.util.Date;
import java.util.List;

import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.Group;

public class GroupAttendanceTO {

	private Date attendenceDate;
	
	private String fromTime;
	
	private String toTime;
	
	private Group group;
	
	private AppUser teacher;
	
	private String remarks;
	
	private List<StudentAttendanceTO> studentAttendances;

	public Date getAttendenceDate() {
		return attendenceDate;
	}

	public void setAttendenceDate(Date attendenceDate) {
		this.attendenceDate = attendenceDate;
	}

	public String getFromTime() {
		return fromTime;
	}

	public void setFromTime(String fromTime) {
		this.fromTime = fromTime;
	}

	public String getToTime() {
		return toTime;
	}

	public void setToTime(String toTime) {
		this.toTime = toTime;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public AppUser getTeacher() {
		return teacher;
	}

	public void setTeacher(AppUser teacher) {
		this.teacher = teacher;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public List<StudentAttendanceTO> getStudentAttendances() {
		return studentAttendances;
	}

	public void setStudentAttendances(List<StudentAttendanceTO> studentAttendances) {
		this.studentAttendances = studentAttendances;
	}
	
}
