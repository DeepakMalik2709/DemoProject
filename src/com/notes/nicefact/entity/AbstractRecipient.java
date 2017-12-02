package com.notes.nicefact.entity;

import javax.persistence.Basic;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;

import com.notes.nicefact.enums.ScheduleAttendeeResponseType;

@MappedSuperclass
public class AbstractRecipient extends CommonEntity {

	private static final long serialVersionUID = 1L;

	public enum RecipientType {
		USER, GROUP, EMAIL
	}

	String email;

	String name;

	String position;

	String department;

	String organization;

	@Enumerated(EnumType.STRING)
	RecipientType type;
	
	public AbstractRecipient(){}
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

	public RecipientType getType() {
		return type;
	}

	public void setType(RecipientType type) {
		this.type = type;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

}
