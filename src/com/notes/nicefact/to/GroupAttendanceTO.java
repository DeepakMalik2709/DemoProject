package com.notes.nicefact.to;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.notes.nicefact.entity.GroupAttendance;
import com.notes.nicefact.entity.StudentAttendance;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupAttendanceTO {

	private Long id;
	
	private long date;
	
	private String fromTime;
	
	private String toTime;
	
	private long groupId;
	
	private String comments;
	
	private List<AttendanceMemberTO> members = new ArrayList<>();;

	public GroupAttendanceTO(){
		
	}

	public GroupAttendanceTO(GroupAttendance attendance ) {
		this.id = attendance.getId();
		this.date = attendance.getDate().getTime();
		this.fromTime = attendance.getFromTime();
		this.toTime = attendance.getToTime();
		this.comments = attendance.getComments();
		AttendanceMemberTO memberTO;
		for(StudentAttendance sa : attendance.getStudentAttendances()){
			memberTO = new AttendanceMemberTO(sa);
			this.members.add(memberTO);
		}
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
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


	public long getGroupId() {
		return groupId;
	}

	public void setGroupId(long groupId) {
		this.groupId = groupId;
	}


	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public List<AttendanceMemberTO> getMembers() {
		return members;
	}

	public void setMembers(List<AttendanceMemberTO> members) {
		this.members = members;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
}
