package com.notes.nicefact.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONException;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventReminder;
import com.google.api.services.calendar.model.Events;
import com.notes.nicefact.content.AllSchoolError;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.Tutorial;
import com.notes.nicefact.exception.AppException;
import com.notes.nicefact.google.GoogleAppUtils;
import com.notes.nicefact.service.AppUserService;
import com.notes.nicefact.service.GoogleCalendarService;
import com.notes.nicefact.service.GroupService;
import com.notes.nicefact.service.TutorialService;
import com.notes.nicefact.to.AppUserTO;
import com.notes.nicefact.to.EventTO;
import com.notes.nicefact.to.EventsTO;
import com.notes.nicefact.to.SearchTO;
import com.notes.nicefact.to.TutorialTO;
import com.notes.nicefact.util.Constants;
import com.notes.nicefact.util.EntityManagerHelper;
import com.notes.nicefact.util.Utils;

@Path("/calendar")
public class CalendarController extends CommonController {

	private final static Logger logger = Logger.getLogger(CalendarController.class.getName());

		
	@POST
	@Path("/updateEvent")
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateEvent(com.notes.nicefact.entity.Event schedule, @Context HttpServletResponse response,@Context HttpServletRequest request) {
		Map<String, Object> json = new HashMap<>();
		try {
			String calendarId = "primary";
			String eventId = schedule.getId();
			
			AppUser user =(AppUser)  request.getSession().getAttribute(Constants.SESSION_KEY_lOGIN_USER);
		//	attendee.setId(user.getu);
			
			
			com.google.api.services.calendar.Calendar service = GoogleAppUtils.getCalendarService();
			if(service!=null){
				Event event = service.events().get(calendarId, eventId).execute(); 
				for(EventAttendee evAtt :  event.getAttendees()){
					if(evAtt.getEmail().equalsIgnoreCase(user.getEmail())){
						evAtt.setResponseStatus(schedule.getAttendees().get(0).getResponseStatus());
					}				
				}
				Event updatedEvent  = service.events().update(calendarId, event.getId(), event).execute();
				json.put(Constants.CODE, Constants.OK);
				json.put(Constants.DATA_ITEM, updatedEvent);
			}else{
				json.put(Constants.CODE, AllSchoolError.GOOGLE_CALENDAR_AUTHORIZATION_NULL_CODE	);
				json.put(Constants.MESSAGE, AllSchoolError.GOOGLE_CALENDAR_AUTHORIZATION_NULL_MESSAGE);	
			}
		} catch (IOException e) {
			json.put(Constants.CODE, Constants.ERROR_WITH_MSG);
			json.put(Constants.MESSAGE, e.getMessage());	
			e.printStackTrace();
		}
		renderResponseJson(json, response);
		logger.info("getPostGroupOrder exit");
	}

	@POST
	@Path("/insertEvent")
	@Consumes(MediaType.APPLICATION_JSON)
	public void createEvent(com.notes.nicefact.entity.Event schedule, @Context HttpServletResponse response,@Context HttpServletRequest request) {
		Map<String, Object> json = new HashMap<>();
		try {
			com.google.api.services.calendar.Calendar service = GoogleAppUtils.getCalendarService();
			logger.info("schedule : "+schedule);
			if(service !=null){
			Event event = new Event().set("sendNotifications", true)
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
			List<EventAttendee> attendees = null;
			EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
			GroupService groupService = new GroupService(em);
			
			SearchTO searchTO = new SearchTO(request, Constants.RECORDS_100);
			if(schedule.getGroups() !=null && schedule.getGroups().size()>0){
				attendees =groupService.fetchMemberEmailFromGroup(schedule.getGroups(),searchTO);
			}
			
			event.setAttendees(attendees);
			
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
			json.put(Constants.CODE, Constants.OK);
			json.put(Constants.DATA_ITEM, event);
			}else{
				json.put(Constants.CODE, AllSchoolError.GOOGLE_CALENDAR_AUTHORIZATION_NULL_CODE	);
				json.put(Constants.MESSAGE, AllSchoolError.GOOGLE_CALENDAR_AUTHORIZATION_NULL_MESSAGE);	
			}
		} catch (IOException e) {
			json.put(Constants.CODE, Constants.ERROR_WITH_MSG);
			json.put(Constants.MESSAGE, e.getMessage());	
			e.printStackTrace();
		}
		renderResponseJson(json, response);
		logger.info("getPostGroupOrder exit");
	    
	}
	
	@GET
	@Path(GoogleCalendarService.CALENDAR_CALLBACK)
	public void googleDriveCallback(@QueryParam("code") String code, @QueryParam("error") String error, @Context HttpServletResponse response, @Context HttpServletRequest request) throws IOException,
			JSONException {
		System.out.println("code :"+code+" error "+error+" response "+response.getStatus());
	}
	

	@GET
	@Path("/calendars")
	public void publicHome(@Context HttpServletRequest request, @Context HttpServletResponse response) throws Exception {

		System.out.println("in google event ");
		Map<String, Object> json = new HashMap<>();
		try {
			com.google.api.services.calendar.Calendar service = GoogleAppUtils.getCalendarService();
			if(service!=null){
				// List the next 10 events from the primary calendar.
				Events events = service.events().list("primary").execute();
				List<Event> items = events.getItems();
				List<EventTO> eventTos = new ArrayList<EventTO>();
	
				if (items.size() == 0) {
					json.put(Constants.CODE, Constants.NO_RESULT);
					System.out.println("No upcoming events found.");
				} else {
	
					System.out.println("Upcoming events");
					for (Event event : items) {
						DateTime start = event.getStart().getDateTime();
						DateTime end = event.getEnd().getDateTime();
						if (start == null) {
							start = event.getStart().getDate();
						}
						if (end == null) {
							end = event.getEnd().getDate();
						}						
						
						eventTos.add(new EventTO(event.getId(), event.getSummary(), start, end, Utils.getRandomColor(),Utils.getRandomColor()));
						System.out.printf("%s (%s)\n", event.getSummary(), start);
					}
					EventsTO eventsTo = new EventsTO("1", eventTos);
					json.put(Constants.CODE, Constants.RESPONSE_OK);
					json.put(Constants.DATA_ITEM, eventsTo);
				}
	
				json.put(Constants.TOTAL, items.size());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.put(Constants.CODE, Constants.ERROR_WITH_MSG);
			json.put(Constants.MESSAGE, e.getMessage());
		}
		renderResponseJson(json, response);
	}
		
	
	
}
