package com.notes.nicefact.entity;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.api.services.calendar.model.EventAttendee;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Event {

	private String id;
	private String title;
	private Date start;
	private String backgroundColor;
	private String borderColor;
	private Date end;
	private String description;
	private String location;
	private String	eventType;
	private List<EventAttendee>	attendees;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Date getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start;
	}
	public String getBackgroundColor() {
		return backgroundColor;
	}
	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	public String getBorderColor() {
		return borderColor;
	}
	public void setBorderColor(String borderColor) {
		this.borderColor = borderColor;
	}
	public Date getEnd() {
		return end;
	}
	public void setEnd(Date end) {
		this.end = end;
	}


	@Override
	public String toString() {
		return "Event [id=" + id + ", title=" + title + ", start=" + start
				+ ", backgroundColor=" + backgroundColor + ", borderColor="
				+ borderColor + ", end=" + end + ", description=" + description
				+ ", eventType=" + eventType +",	location"+ location +"]";
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public List<EventAttendee> getAttendees() {
		return attendees;
	}
	public void setAttendees(List<EventAttendee> attendees) {
		this.attendees = attendees;
	}

}
