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
import com.google.api.services.calendar.model.AclRule;
import com.google.api.services.calendar.model.AclRule.Scope;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.google.api.services.calendar.model.Events;
import com.notes.nicefact.content.AllSchoolError;
import com.notes.nicefact.content.AllSchoolException;
import com.notes.nicefact.controller.CalendarController;
import com.notes.nicefact.dao.CommonDAO;
import com.notes.nicefact.dao.GroupDAO;
import com.notes.nicefact.dao.PostDAO;
import com.notes.nicefact.dao.PostRecipientDAO;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.Group;
import com.notes.nicefact.entity.GroupMember;
import com.notes.nicefact.entity.Post;
import com.notes.nicefact.entity.PostRecipient;
import com.notes.nicefact.exception.UnauthorizedException;
import com.notes.nicefact.google.GoogleAppUtils;
import com.notes.nicefact.to.EventTO;
import com.notes.nicefact.to.PostRecipientTO;
import com.notes.nicefact.to.PostTO;
import com.notes.nicefact.to.SearchTO;
import com.notes.nicefact.util.CacheUtils;
import com.notes.nicefact.util.Constants;
import com.notes.nicefact.util.CurrentContext;
import com.notes.nicefact.util.Utils;

public class ScheduleService extends CommonService<Post> {
	private final static Logger logger = Logger.getLogger(CalendarController.class.getName());
	BackendTaskService backendTaskService;
	GroupService groupService;
	PostService postService;
	PostRecipientDAO postRecipientDAO;
	PostDAO postDAO;
	EntityManager em;
	
	public ScheduleService(EntityManager em) {
		this.em = em;
		backendTaskService = new BackendTaskService(em);
		groupService = new GroupService(em);
		postService = new PostService(em);
		postRecipientDAO = new PostRecipientDAO(em);
		postDAO = new PostDAO(em);
	}

	public Event updateEvent(com.notes.nicefact.to.Event schedule, AppUser user) throws IOException, AllSchoolException {
		String calendarId = "primary";
		String eventId = schedule.getId();
		Event updatedEvent = null;
		// attendee.setId(user.getu);
		com.google.api.services.calendar.Calendar service = GoogleAppUtils.getCalendarService();
		if (service != null) {
			Event event = service.events().get(calendarId, eventId).execute();
			for (EventAttendee evAtt : event.getAttendees()) {
				if (evAtt.getEmail().equalsIgnoreCase(user.getEmail())) {
					evAtt.setResponseStatus(schedule.getAttendees().get(0).getResponseStatus());
				}
			}
			updatedEvent = service.events().update(calendarId, event.getId(), event).execute();
		} else {
			throw new AllSchoolException(AllSchoolError.GOOGLE_CALENDAR_AUTHORIZATION_NULL_CODE, AllSchoolError.GOOGLE_CALENDAR_AUTHORIZATION_NULL_MESSAGE);

		}
		return updatedEvent;
	}

	public List<PostTO> createEvent(com.notes.nicefact.to.Event schedule,
			AppUser user) throws IOException, AllSchoolException {
		com.google.api.services.calendar.Calendar service = GoogleAppUtils
				.getCalendarService();

		Event event = null;
		if (service != null) {
			event = new Event().set("sendNotifications", true).setSummary(schedule.getTitle()).setLocation(schedule.getLocation()).setDescription(schedule.getDescription());
			if (schedule.getStart() == null) {
				schedule.setStart(new Date());
			}
			DateTime startDateTime = new DateTime(schedule.getStart());
			EventDateTime start = new EventDateTime().setDateTime(startDateTime).setTimeZone(Constants.INDIA_TIMEZONE);
			event.setStart(start);
			if (schedule.getEnd() == null) {
				schedule.setEnd(new Date());
			}
			DateTime endDateTime = new DateTime(schedule.getEnd());
			EventDateTime end = new EventDateTime().setDateTime(endDateTime).setTimeZone(Constants.INDIA_TIMEZONE);
			event.setEnd(end);
			
			String[] recurrence = Utils.createGoogleRecurrenceStr(schedule.getWeekdays());
			event.setRecurrence(Arrays.asList(recurrence));

			event.setAttendees(schedule.getAttendees());

			EventReminder[] reminderOverrides = new EventReminder[] { new EventReminder().setMethod("email").setMinutes(24 * 60), new EventReminder().setMethod("popup").setMinutes(10), };
			Event.Reminders reminders = new Event.Reminders().setUseDefault(false).setOverrides(Arrays.asList(reminderOverrides));
			event.setReminders(reminders);

			/*String calendarId = "primary";
			event = service.events().insert(calendarId, event).execute();*/
			schedule.setGoogleEventId(event.getId());
			System.out.printf("Event created: %s\n", event.getHtmlLink());

		} else {
			throw new AllSchoolException(AllSchoolError.GOOGLE_CALENDAR_AUTHORIZATION_NULL_CODE, AllSchoolError.GOOGLE_CALENDAR_AUTHORIZATION_NULL_MESSAGE);

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
				logger.error(e.getMessage(),e);
				throw new AllSchoolException(AllSchoolError.SCHEDULE_CREATION_ERROR_CODE,AllSchoolError.SCHEDULE_CREATION_ERROR_MESSAGE);
			}
		}
		

		logger.info("createEvent : " + postTos.size());
		return postTos;
	}


	@Override
	protected CommonDAO<Post> getDAO() {
		return postDAO;
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

	public PostRecipient updateScheduleResponse(PostRecipientTO postRecipientTO, AppUser user) {
		PostRecipient postRecipientDB = null;
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
				throw new UnauthorizedException(AllSchoolError.EDIT_PERMISSION_ERROR_CODE, AllSchoolError.EDIT_PERMISSION_ERROR_MESSAGE);
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
			logger.error(e.getMessage(),e);
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
			logger.error(e.getMessage(),e);
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

	public void createCalendar(Group group) {
		AppUser groupAdmin =null;
		for(String admin : group.getAdmins()){
			groupAdmin = CacheUtils.getAppUser(admin);
			if(groupAdmin.getUseGoogleCalendar()){
				break;
			}
		}
		
		if(null == groupAdmin || !groupAdmin.getUseGoogleCalendar()){
			logger.error("Cannot create calendar for group : " + group.getId() + " , " + group.getName());
		}else{
			
			try {
				Utils.refreshToken(groupAdmin);
				com.google.api.services.calendar.Calendar service = GoogleAppUtils.getCalendarService(groupAdmin);
				com.google.api.services.calendar.model.Calendar calendar =   new com.google.api.services.calendar.model.Calendar();
				calendar.setSummary(group.getName());
				calendar.setTimeZone(Constants.INDIA_TIMEZONE);
				com.google.api.services.calendar.model.Calendar createdCalendar =service.calendars().insert(calendar).execute();
				if(null ==createdCalendar){
					logger.error("calendar creation failed for group : " + group.getId() + " , " + group.getName());
				}else{
					logger.info("calendar created for group : " + group.getId() + " , " + group.getName() + " , cal id : " +  createdCalendar.getId());
					GroupService groupService = new GroupService(em);
					Group db = groupService.get(group.getId());
					db.setCalendarId(createdCalendar.getId());
					groupService.upsert(db);
					group.setCalendarId(createdCalendar.getId());
					/* make calendar public */
					AclRule rule = new AclRule();
					Scope scope = new Scope();
					scope.setType("default");
					rule.setScope(scope).setRole("reader");

					// Insert new access rule
					AclRule createdRule = service.acl().insert(createdCalendar.getId(), rule).execute();
					if(createdRule == null){
						logger.error("cannot make calendar public : " + group.getId() + " , " + group.getName() + " , cal id : " +  createdCalendar.getId());
					}
					
					for(String admin : group.getAdmins()){
						if (!admin.equals(groupAdmin.getEmail())) {
							rule = new AclRule();
							scope = new Scope();
							scope.setType("user").setValue(admin);
							rule.setScope(scope).setRole("owner");
							// Insert new access rule
							createdRule = service.acl().insert(createdCalendar.getId(), rule).execute();
							if (createdRule == null) {
								logger.error("cannot add user as owner to calendar : " + group.getId() + " , " + group.getName() + " , cal id : " + createdCalendar.getId());
							} 
						}
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
		}
		
	}

	public void createGroupEvent(Group group, Post post) {
		AppUser groupAdmin =  CacheUtils.getAppUser(post.getCreatedBy());

		if (null == groupAdmin || !groupAdmin.getUseGoogleCalendar()) {
			logger.error("Cannot create event for admin : " + groupAdmin + ", post : " + post.getId() + " , " + post.getTitle());
		} else {

			try {
				Utils.refreshToken(groupAdmin);
				com.google.api.services.calendar.Calendar calService = GoogleAppUtils.getCalendarService(groupAdmin);
				Event event = new Event().set("sendNotifications", true).setSummary(post.getTitle()).setLocation(post.getLocation()).setDescription(post.getComment());
				Date today = new Date();
				DateTime startDateTime = new DateTime(post.getFromDate() == null ? today : post.getFromDate());
				EventDateTime start = new EventDateTime().setDateTime(startDateTime).setTimeZone(Constants.INDIA_TIMEZONE);
				event.setStart(start);

				DateTime endDateTime = new DateTime(post.getToDate() == null ? today : post.getToDate());
				EventDateTime end = new EventDateTime().setDateTime(endDateTime).setTimeZone(Constants.INDIA_TIMEZONE);
				event.setEnd(end);

				if (!post.getWeekdays().isEmpty()) {
					String[] recurrence = Utils.createGoogleRecurrenceStr(post.getWeekdays());
					event.setRecurrence(Arrays.asList(recurrence));
				}

				EventReminder[] reminderOverrides = new EventReminder[] { new EventReminder().setMethod("email").setMinutes(24 * 60), new EventReminder().setMethod("popup").setMinutes(10), };
				Event.Reminders reminders = new Event.Reminders().setUseDefault(false).setOverrides(Arrays.asList(reminderOverrides));
				event.setReminders(reminders);
				EventAttendee attendee;
				List<EventAttendee> attendees = new ArrayList<>();
				for (GroupMember member : group.getMembers()) {
					if (!groupAdmin.getEmail().equals(member.getEmail())) {
						attendee = new EventAttendee();
						attendee = attendee.setEmail(member.getEmail());
						attendees.add(attendee);
					}
				}
				if (!attendees.isEmpty()) {
					event.setAttendees(attendees);
				}

				event = calService.events().insert(group.getCalendarId(), event).execute();
				post.setGoogleEventId(event.getId());
				postDAO.upsert(post);
				logger.info("Event created:: " + event.getHtmlLink());

			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

}
