package com.notes.nicefact.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.notes.nicefact.content.AllSchoolError;
import com.notes.nicefact.content.AllSchoolException;
import com.notes.nicefact.controller.CalendarController;
import com.notes.nicefact.dao.CommonDAO;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.Group;
import com.notes.nicefact.entity.Post;
import com.notes.nicefact.google.GoogleAppUtils;
import com.notes.nicefact.to.PostTO;
import com.notes.nicefact.util.Constants;
import com.notes.nicefact.util.CurrentContext;
import com.notes.nicefact.util.EntityManagerHelper;

public class ScheduleService extends CommonService<Group> {
	private final static Logger logger = Logger.getLogger(CalendarController.class.getName());
	BackendTaskService backendTaskService ;
	GroupService groupService ;
	PostService postService;
	
	public ScheduleService(EntityManager em) {
		backendTaskService = new BackendTaskService(em);
		groupService = new GroupService(em);
		postService = new PostService(em);
	}

	public Event updateEvent(com.notes.nicefact.entity.Event schedule,AppUser user ) throws IOException, AllSchoolException {
		String calendarId = "primary";
		String eventId = schedule.getId();
		Event updatedEvent  =null;
	//	attendee.setId(user.getu);
		com.google.api.services.calendar.Calendar service = GoogleAppUtils.getCalendarService();
		if(service!=null){
			Event event = service.events().get(calendarId, eventId).execute(); 
			for(EventAttendee evAtt :  event.getAttendees()){
				if(evAtt.getEmail().equalsIgnoreCase(user.getEmail())){
					evAtt.setResponseStatus(schedule.getAttendees().get(0).getResponseStatus());
				}				
			}
			updatedEvent  = service.events().update(calendarId, event.getId(), event).execute();
		}else{
			throw new AllSchoolException(AllSchoolError.GOOGLE_CALENDAR_AUTHORIZATION_NULL_CODE	, AllSchoolError.GOOGLE_CALENDAR_AUTHORIZATION_NULL_MESSAGE);

		}
		return updatedEvent;
	}

	public Event createEvent(com.notes.nicefact.entity.Event schedule,AppUser user) throws IOException, AllSchoolException {
		com.google.api.services.calendar.Calendar service = GoogleAppUtils.getCalendarService();
		
			PostTO postTo = new PostTO(schedule,user);
			Post post = postService.upsert(postTo, CurrentContext.getAppUser());
			PostTO savedTO = new PostTO(post);
			
		
		logger.info("createEvent : "+schedule);
		Event event= null;
		if(service !=null){
		 event = new Event().set("sendNotifications", true)
		    .setSummary(schedule.getTitle())
		    .setLocation(schedule.getLocation())
		    .setDescription(schedule.getDescription());
		if(schedule.getStart() ==null){
			schedule.setStart(new Date());
		}
		DateTime startDateTime = new DateTime(schedule.getStart());
		EventDateTime start = new EventDateTime()
		    .setDateTime(startDateTime)
		    .setTimeZone("America/Los_Angeles");
		event.setStart(start);
		if(schedule.getEnd() ==null){
			schedule.setEnd(new Date());
		}
		DateTime endDateTime = new DateTime(schedule.getEnd());
		EventDateTime end = new EventDateTime()
		    .setDateTime(endDateTime)
		    .setTimeZone("America/Los_Angeles");
		event.setEnd(end);
		
		String[] recurrence = new String[] {"RRULE:FREQ=DAILY;COUNT=2"};
		event.setRecurrence(Arrays.asList(recurrence));
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		
		
		
		
		event.setAttendees(schedule.getAttendees());
		
		EventReminder[] reminderOverrides = new EventReminder[] {
		    new EventReminder().setMethod("email").setMinutes(24 * 60),
		    new EventReminder().setMethod("popup").setMinutes(10),
		};
		Event.Reminders reminders = new Event.Reminders()
		    .setUseDefault(false)
		    .setOverrides(Arrays.asList(reminderOverrides));
		event.setReminders(reminders);
		
		String calendarId = "primary";
		event = service.events().insert(calendarId, event).execute();
		System.out.printf("Event created: %s\n", event.getHtmlLink());
		
		}else{
			throw new AllSchoolException(AllSchoolError.GOOGLE_CALENDAR_AUTHORIZATION_NULL_CODE	, AllSchoolError.GOOGLE_CALENDAR_AUTHORIZATION_NULL_MESSAGE);

		}
		return event;
	}
	
	@Override
	protected CommonDAO<Group> getDAO() {
		// TODO Auto-generated method stub
		return null;
	}

	public BackendTaskService getBackendTaskService() {
		return backendTaskService;
	}

	public void setBackendTaskService(BackendTaskService backendTaskService) {
		this.backendTaskService = backendTaskService;
	}

	public GroupService getGroupService() {
		return groupService;
	}

	public void setGroupService(GroupService groupService) {
		this.groupService = groupService;
	}
}
