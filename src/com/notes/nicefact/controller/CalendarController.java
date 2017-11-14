package com.notes.nicefact.controller;

import java.io.IOException;
import java.util.ArrayList;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.json.JSONException;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.notes.nicefact.content.AllSchoolException;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.google.GoogleAppUtils;
import com.notes.nicefact.service.GoogleCalendarService;
import com.notes.nicefact.service.ScheduleService;
import com.notes.nicefact.service.GroupService;
import com.notes.nicefact.service.TutorialService;
import com.notes.nicefact.service.GoogleDriveService.FOLDER;
import com.notes.nicefact.to.AppUserTO;
import com.notes.nicefact.to.EventTO;
import com.notes.nicefact.to.EventsTO;
import com.notes.nicefact.to.MoveFileTO;
import com.notes.nicefact.to.SearchTO;
import com.notes.nicefact.util.Constants;
import com.notes.nicefact.util.EntityManagerHelper;
import com.notes.nicefact.util.Utils;
import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;

@Path("/calendar")
public class CalendarController extends CommonController {

	private final static Logger logger = Logger.getLogger(CalendarController.class.getName());

	@POST
	@Path("/updateEvent")
	@Consumes(MediaType.APPLICATION_JSON)
	public void updateEvent(com.notes.nicefact.entity.Event schedule, @Context HttpServletResponse response,
			@Context HttpServletRequest request) {
		logger.info("reactToschedule start , postId : ");
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			ScheduleService scheduleService = new ScheduleService(em);
			AppUser user = (AppUser) request.getSession().getAttribute(Constants.SESSION_KEY_lOGIN_USER);
			Event updatedEvent = scheduleService.updateEvent(schedule, user);
			json.put(Constants.CODE, Constants.RESPONSE_OK);
			json.put(Constants.DATA_ITEM, updatedEvent);
		} catch (AllSchoolException e) {
			logger.error(e.getMessage(), e);

			json.put(Constants.CODE, e.getErrorCode());
			json.put(Constants.MESSAGE, e.getErrorMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);

			json.put(Constants.CODE, Constants.ERROR_WITH_MSG);
			json.put(Constants.MESSAGE, e.getMessage());
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
		renderResponseJson(json, response);
		logger.info("reactToschedule exit");
	}

	/*
	 * jkb : use following code to move files to shcedule foleer
	 * 
	 * MoveFileTO moveFileTO =
	 * MoveFileTO.getInstances().setFileOwner(user.getEmail()).addParents(
	 * FOLDER.Attachments, FOLDER.Schedule).setUser(user);
	 * 
	 * add all fiels to moveFileTO moveFileTO.addFileIds(driveFile.getId());
	 * 
	 * after all files are added run driveService.moveFile(moveFileTO);
	 */
	@POST
	@Path("/insertEvent")
	@Consumes(MediaType.APPLICATION_JSON)
	public void createEvent(com.notes.nicefact.entity.Event schedule, @Context HttpServletResponse response,
			@Context HttpServletRequest request) {
		logger.info("createEvent start , postId : ");
		System.out.println("schedule :" + schedule);
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			ScheduleService scheduleService = new ScheduleService(em);
			AppUser user = (AppUser) request.getSession().getAttribute(Constants.SESSION_KEY_lOGIN_USER);
			SearchTO searchTO = new SearchTO(request, Constants.RECORDS_100);
			/*
			 * if(schedule.getGroups() !=null && schedule.getGroups().size()>0){
			 * schedule.setAttendees(scheduleService.getGroupService().
			 * fetchMemberEmailFromGroup(schedule.getGroups(),searchTO)); }
			 */
			Event createdEvent = scheduleService.createEvent(schedule, user);
			json.put("event", createdEvent);
		} catch (AllSchoolException e) {
			logger.error(e.getMessage(), e);

			json.put(Constants.CODE, e.getErrorCode());
			json.put(Constants.MESSAGE, e.getErrorMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);

			json.put(Constants.CODE, Constants.ERROR_WITH_MSG);
			json.put(Constants.MESSAGE, e.getMessage());
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
		renderResponseJson(json, response);
		logger.info("createEvent exit");

	}

	@GET
	@Path(GoogleCalendarService.CALENDAR_CALLBACK)
	public void googleDriveCallback(@QueryParam("code") String code, @QueryParam("error") String error,
			@Context HttpServletResponse response, @Context HttpServletRequest request) throws IOException,
			JSONException {
		System.out.println("code :" + code + " error " + error + " response " + response.getStatus());
	}

	@GET
	@Path("/calendars")
	public void publicHome(@Context HttpServletRequest request, @Context HttpServletResponse response) {

		System.out.println("in google event ");
		Map<String, Object> json = new HashMap<>();
		try {
			com.google.api.services.calendar.Calendar service = GoogleAppUtils.getCalendarService();
			if (service != null) {
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

						eventTos.add(new EventTO(event.getId(), event.getSummary(), "", start, end, Utils
								.getRandomColor(), Utils.getRandomColor()));
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
