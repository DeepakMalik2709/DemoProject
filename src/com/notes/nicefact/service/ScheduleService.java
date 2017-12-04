package com.notes.nicefact.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.google.api.services.calendar.model.Events;
import com.notes.nicefact.content.AllSchoolError;
import com.notes.nicefact.content.AllSchoolException;
import com.notes.nicefact.controller.CalendarController;
import com.notes.nicefact.dao.CommonDAO;
import com.notes.nicefact.dao.PostRecipientDAO;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.Group;
import com.notes.nicefact.entity.Post;
import com.notes.nicefact.entity.PostRecipient;
import com.notes.nicefact.exception.UnauthorizedException;
import com.notes.nicefact.google.GoogleAppUtils;
import com.notes.nicefact.to.EventTO;
import com.notes.nicefact.to.PostRecipientTO;
import com.notes.nicefact.to.PostTO;
import com.notes.nicefact.to.SearchTO;
import com.notes.nicefact.util.CurrentContext;
import com.notes.nicefact.util.Utils;

public class ScheduleService extends CommonService<Group> {
	private final static Logger logger = Logger
			.getLogger(CalendarController.class.getName());
	BackendTaskService backendTaskService;
	GroupService groupService;
	PostService postService;
	PostRecipientDAO postRecipientDAO;
	
	public ScheduleService(EntityManager em) {
		backendTaskService = new BackendTaskService(em);
		groupService = new GroupService(em);
		postService = new PostService(em);
		postRecipientDAO = new PostRecipientDAO(em);
	}

	public Event updateEvent(com.notes.nicefact.entity.Event schedule,
			AppUser user) throws IOException, AllSchoolException {
		String calendarId = "primary";
		String eventId = schedule.getId();
		Event updatedEvent = null;
		// attendee.setId(user.getu);
		com.google.api.services.calendar.Calendar service = GoogleAppUtils
				.getCalendarService();
		if (service != null) {
			Event event = service.events().get(calendarId, eventId).execute();
			for (EventAttendee evAtt : event.getAttendees()) {
				if (evAtt.getEmail().equalsIgnoreCase(user.getEmail())) {
					evAtt.setResponseStatus(schedule.getAttendees().get(0)
							.getResponseStatus());
				}
			}
			updatedEvent = service.events()
					.update(calendarId, event.getId(), event).execute();
		} else {
			throw new AllSchoolException(
					AllSchoolError.GOOGLE_CALENDAR_AUTHORIZATION_NULL_CODE,
					AllSchoolError.GOOGLE_CALENDAR_AUTHORIZATION_NULL_MESSAGE);

		}
		return updatedEvent;
	}

	public List<PostTO> createEvent(com.notes.nicefact.entity.Event schedule,
			AppUser user) throws IOException, AllSchoolException {
		com.google.api.services.calendar.Calendar service = GoogleAppUtils
				.getCalendarService();

		
		Event event = null;
		if (service != null) {
			event = new Event().set("sendNotifications", true)
					.setSummary(schedule.getTitle())
					.setLocation(schedule.getLocation())
					.setDescription(schedule.getDescription());
			if (schedule.getStart() == null) {
				schedule.setStart(new Date());
			}
			DateTime startDateTime = new DateTime(schedule.getStart());
			EventDateTime start = new EventDateTime()
					.setDateTime(startDateTime).setTimeZone(
							"America/Los_Angeles");
			event.setStart(start);
			if (schedule.getEnd() == null) {
				schedule.setEnd(new Date());
			}
			DateTime endDateTime = new DateTime(schedule.getEnd());
			EventDateTime end = new EventDateTime().setDateTime(endDateTime)
					.setTimeZone("America/Los_Angeles");
			event.setEnd(end);
			
			String[] recurrence = createGoogleRecurrenceStr(schedule.getWeekdays());
			event.setRecurrence(Arrays.asList(recurrence));

			event.setAttendees(schedule.getAttendees());

			EventReminder[] reminderOverrides = new EventReminder[] {
					new EventReminder().setMethod("email").setMinutes(24 * 60),
					new EventReminder().setMethod("popup").setMinutes(10), };
			Event.Reminders reminders = new Event.Reminders().setUseDefault(
					false).setOverrides(Arrays.asList(reminderOverrides));
			event.setReminders(reminders);

			String calendarId = "primary";
			event = service.events().insert(calendarId, event).execute();
			schedule.setGoogleEventId(event.getId());
			System.out.printf("Event created: %s\n", event.getHtmlLink());

		} else {
			throw new AllSchoolException(
					AllSchoolError.GOOGLE_CALENDAR_AUTHORIZATION_NULL_CODE,
					AllSchoolError.GOOGLE_CALENDAR_AUTHORIZATION_NULL_MESSAGE);

		}
		List<PostTO> postTos = new ArrayList<PostTO>();
		for (String day : schedule.getWeekdays()) {
			try{
			PostTO postTo = new PostTO(schedule, user);
			postTo.setWeekDay(day);
			Post post = postService.upsert(postTo, CurrentContext.getAppUser());
			PostTO savedTO = new PostTO(post);
			postTos.add(savedTO);
			}catch(Exception e){
				throw new AllSchoolException(AllSchoolError.SCHEDULE_CREATION_ERROR_CODE,AllSchoolError.SCHEDULE_CREATION_ERROR_MESSAGE);
			}
		}
		

		logger.info("createEvent : " + postTos.size());
		return postTos;
	}

	private String[] createGoogleRecurrenceStr(List<String> list) {
		String recurStr="RRULE:FREQ=WEEKLY;BYDAY=";
		for (String day : list) {
			recurStr+=day.substring(0, 2)+",";
		}
		String[] recurrence = new String[] { recurStr.substring(0,recurStr.length()-1) };
		return recurrence;
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

	public PostRecipient updateScheduleResponse(PostRecipientTO postRecipientTO, AppUser user)  {
		PostRecipient postRecipientDB =null;
		if (null == postRecipientTO.getId() || postRecipientTO.getId() <= 0) {
			postRecipientTO.setEmail(user.getEmail());
			Post post = postService.get(postRecipientTO.getPostId());
			postRecipientTO.setType("USER");
			postRecipientDB = new PostRecipient(postRecipientTO);	
			postRecipientDB.setPost(post);
		} else {
			postRecipientDB = postRecipientDAO.get(postRecipientTO.getId());
			if (postRecipientDB.getCreatedBy().equals(user.getEmail())) {
				postRecipientDB.updateProps(postRecipientTO);	
			} else {
				throw new UnauthorizedException(AllSchoolError.EDIT_PERMISSION_ERROR_CODE ,AllSchoolError.EDIT_PERMISSION_ERROR_MESSAGE );
			}
		}
		postRecipientDB = postRecipientDAO.upsert(postRecipientDB);
		return postRecipientDB;
	}

	public List<EventTO> getEventFromGoogleCalendar() throws AllSchoolException, IOException {
		List<EventTO> eventTos = new ArrayList<EventTO>();
		try {
		com.google.api.services.calendar.Calendar service = GoogleAppUtils.getCalendarService();
		if (service != null) {
			// List the next 10 events from the primary calendar.
			Events events= service.events().list("primary").execute();			
			List<Event> items = events.getItems();
			if (items.size() != 0) {
				System.out.println("Upcoming events");
				for (Event event : items) {
					EventTO  eventTO = convertToEventTO(event);
					eventTos.add(eventTO);
				}
			}
			
		}
		} catch (IOException e) {
			e.printStackTrace();
			throw new AllSchoolException(AllSchoolError.GOOGLE_CAL_EVENT_FETCH_ERROR_CODE,AllSchoolError.GOOGLE_CAL_EVENT_FETCH_ERROR_MESSAGE);
		
		}
		return eventTos;
	}

	public List<EventTO> fetchScheduleByDate(SearchTO searchTO,Date date) {
		List<EventTO> eventTos = new ArrayList<EventTO>();
		List<PostTO> postTos = postService.fetchScheduleByDate(searchTO,date);
		for(PostTO postTo : postTos){
			EventTO eventTo = new EventTO(postTo.getId()+"", postTo.getTitle(), "",new DateTime(postTo.getFromDate()), new DateTime(postTo.getToDate()), 
											Utils.getRandomColor(), Utils.getRandomColor());
			eventTos.add(eventTo);
		}
		return eventTos;
	}

	public Events fetchTodayGoogleEvents() throws AllSchoolException {		
		Events events=null;
		try {			
			com.google.api.services.calendar.Calendar service = GoogleAppUtils.getCalendarService();
			if (service != null) {
				// List the next 10 events from the primary calendar.
				Calendar cal = Calendar.getInstance();			
				cal.set(Calendar.HOUR, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				DateTime today = new DateTime(cal.getTime());
				cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR)+1);
				DateTime tomorrow = new DateTime(cal.getTime());			
		        events = service.events().list("primary").setTimeMin(today).setTimeMax(tomorrow)
		            .setSingleEvents(true).execute();	
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new AllSchoolException(AllSchoolError.GOOGLE_CAL_EVENT_FETCH_ERROR_CODE,AllSchoolError.GOOGLE_CAL_EVENT_FETCH_ERROR_MESSAGE);
		
		}
		return events;
	}

	public int countScheduleByDateAndDay(Date date) {
		return postService.countScheduleByDateAndDay(date);		
	}

	public EventTO convertToEventTO(Event event) {
		DateTime start = event.getStart().getDateTime();
		DateTime end = event.getEnd().getDateTime();
		if (start == null) {
			start = event.getStart().getDate();
		}
		if (end == null) {
			end = event.getEnd().getDate();
		}		
		return new EventTO(event.getId(), event.getSummary(), "--", start, end, Utils.getRandomColor(),Utils.getRandomColor());		 
	}

	public int countTodaysGoogleEvent() throws AllSchoolException {
		 Events events =fetchTodayGoogleEvents();
		 if(events ==null){
			 return 0;
		 } 		  
		 return  events.getItems().size();		
	}
}
