package com.notes.nicefact.controller;

import java.io.IOException;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;
import org.json.JSONException;

import com.google.api.services.calendar.model.Event;
import com.notes.nicefact.content.AllSchoolException;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.PostRecipient;
import com.notes.nicefact.service.GoogleCalendarService;
import com.notes.nicefact.service.ScheduleService;
import com.notes.nicefact.to.EventTO;
import com.notes.nicefact.to.EventsTO;
import com.notes.nicefact.to.PostRecipientTO;
import com.notes.nicefact.to.PostTO;
import com.notes.nicefact.to.SearchTO;
import com.notes.nicefact.util.Constants;
import com.notes.nicefact.util.EntityManagerHelper;

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
			Event updatedEvent =  scheduleService.updateEvent(schedule, user);
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
			List<PostTO> createdEvents = scheduleService.createEvent(schedule, user);		
			json.put(Constants.CODE, Constants.RESPONSE_OK);
			json.put(Constants.DATA_ITEMS, createdEvents);
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
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		System.out.println("in google event ");
		Map<String, Object> json = new HashMap<>();
		try {
			SearchTO searchTO = new SearchTO(request, Constants.RECORDS_20);
			ScheduleService scheduleService = new ScheduleService(em);
			List<EventTO> eventTos = scheduleService.getEventFromGoogleCalendar();
			eventTos.addAll(scheduleService.fetchScheduleByDate(searchTO,new Date()));
			EventsTO eventsTo = new EventsTO("1", eventTos);
			json.put(Constants.CODE, Constants.RESPONSE_OK);
			json.put(Constants.DATA_ITEM, eventsTo);
			
			
		}catch (AllSchoolException e) {
			logger.error(e.getMessage(), e);

			json.put(Constants.CODE, e.getErrorCode());
			json.put(Constants.MESSAGE, e.getErrorMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.put(Constants.CODE, Constants.ERROR_WITH_MSG);
			json.put(Constants.MESSAGE, e.getMessage());
		}
		renderResponseJson(json, response);
	}
	
	@GET
	@Path("/todayScheduleCount")
	public void fetchtodayScheduleCount(@Context HttpServletRequest request, @Context HttpServletResponse response) {
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		System.out.println("in google event ");
		Map<String, Object> json = new HashMap<>();
		try {
			ScheduleService scheduleService = new ScheduleService(em);
			int eventCount = scheduleService.countTodaysGoogleEvent();
			int scheduleCount = scheduleService.countScheduleByDateAndDay(new Date());			
			json.put(Constants.CODE, Constants.RESPONSE_OK);
			json.put(Constants.TOTAL, scheduleCount+eventCount);
			
			
		}catch (AllSchoolException e) {
			logger.error(e.getMessage(), e);

			json.put(Constants.CODE, e.getErrorCode());
			json.put(Constants.MESSAGE, e.getErrorMessage());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			json.put(Constants.CODE, Constants.ERROR_WITH_MSG);
			json.put(Constants.MESSAGE, e.getMessage());
		}
		renderResponseJson(json, response);
	}

	
	@POST
	@Path("/scheduleResponse")
	@Consumes(MediaType.APPLICATION_JSON)
	public void scheduleResponse(PostRecipientTO postRecipientTO, @Context HttpServletResponse response,@Context HttpServletRequest request) {
		logger.info("reactToschedule start , postId : ");
		Map<String, Object> json = new HashMap<>();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		
		try {
			ScheduleService scheduleService = new ScheduleService(em);
			AppUser user = (AppUser) request.getSession().getAttribute(Constants.SESSION_KEY_lOGIN_USER);
			PostRecipient postRecipient =  scheduleService.updateScheduleResponse(postRecipientTO, user);
			PostRecipientTO saved=new PostRecipientTO(postRecipient);
			json.put(Constants.CODE, Constants.RESPONSE_OK);
			json.put(Constants.DATA_ITEM, saved);
		}  catch (Exception e) {
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
}