package com.notes.nicefact.to;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EventsTO {

	private String id;
	private List<EventTO> eventTOs;
	
	public EventsTO(){}
	
	
	public String getId() {
		return id;
	}

	public EventsTO(String id, List<EventTO> eventTOs) {
		super();
		this.id = id;
		this.eventTOs = eventTOs;
	}


	@Override
	public String toString() {
		return "EventsTO [id=" + id + ", eventTOs=" + eventTOs + "]";
	}


	public List<EventTO> getEventTOs() {
		return eventTOs;
	}


	public void setEventTOs(List<EventTO> eventTOs) {
		this.eventTOs = eventTOs;
	}


	public void setId(String id) {
		this.id = id;
	}

}
