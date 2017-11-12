package com.notes.nicefact.to;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.api.client.util.DateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EventTO {

	private String id;
	private String title;
	private Date start;
	private String backgroundColor;
	private String borderColor;
	private Date end;
	private String description;
	private String	eventType;
	
	public EventTO(String id,String title,String eventType, DateTime start, DateTime end, String backgroundColor,
			String borderColor){
		super();
		this.id = id;
		this.title = title;
		this.eventType = eventType;
		this.start = new Date(start.getValue());
		this.backgroundColor = backgroundColor;
		this.borderColor = borderColor;
		this.end =new Date(end.getValue());
	}
	
	public EventTO(String id,String title,String eventType, Date start, Date end, String backgroundColor,
			String borderColor) {
		super();
		this.id = id;
		this.title = title;
		this.eventType = eventType;
		this.start = start;
		this.backgroundColor = backgroundColor;
		this.borderColor = borderColor;
		this.end = end;
	}
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
		return "EventTO [title=" + title + ", start=" + start
				+ ", backgroundColor=" + backgroundColor + ", borderColor="
				+ borderColor + ", end=" + end + "]";
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

}
