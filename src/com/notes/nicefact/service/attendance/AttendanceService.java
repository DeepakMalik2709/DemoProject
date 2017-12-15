package com.notes.nicefact.service.attendance;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

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
import com.notes.nicefact.entity.StudentAttendance;
import com.notes.nicefact.google.GoogleAppUtils;
import com.notes.nicefact.service.BackendTaskService;
import com.notes.nicefact.service.CommonService;
import com.notes.nicefact.service.GroupService;
import com.notes.nicefact.service.PostService;
import com.notes.nicefact.service.StudentAttendenceService;
import com.notes.nicefact.to.GroupAttendanceTO;
import com.notes.nicefact.to.PostTO;
import com.notes.nicefact.util.CacheUtils;
import com.notes.nicefact.util.Constants;
import com.notes.nicefact.util.CurrentContext;
import com.notes.nicefact.util.DateUtils;

public class AttendanceService extends CommonService<Group> {
	private final static Logger logger = Logger.getLogger(CalendarController.class.getName());
	BackendTaskService backendTaskService ;
	GroupService groupService ;
	PostService postService;
	StudentAttendenceService studentAttendenceService;
	
	public AttendanceService(EntityManager em) {
		backendTaskService = new BackendTaskService(em);
		groupService = new GroupService(em);
		postService = new PostService(em);
		studentAttendenceService = new StudentAttendenceService(em);
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

	public void createMapsForAttendanceReport(Map<Long, GroupAttendanceTO> groupAttendanceIdObjectMap, 
			List<StudentAttendance> allStudentAttendanceForGroup, Map<Long, String> attendanceDatesString,
			Map<String, Map<Long,StudentAttendance>> studentDateAttendanceMap, Map<String,String> studentIdNameMap) {
		
		Set<Long> attendanceDates = new HashSet<>();
		for (StudentAttendance studentAttendance : allStudentAttendanceForGroup){
			Map<Long,StudentAttendance> dateAttendanceMap = null;
			if(studentDateAttendanceMap.containsKey(studentAttendance.getEmail())){
				dateAttendanceMap = studentDateAttendanceMap.get(studentAttendance.getEmail());
			}else{
				dateAttendanceMap = new HashMap<>();
			}
			
			GroupAttendanceTO groupAttendanceTO = null;
			if(groupAttendanceIdObjectMap.containsKey(studentAttendance.getGroupAttendance().getId())){
				groupAttendanceTO = groupAttendanceIdObjectMap.get(studentAttendance.getGroupAttendance().getId());
			}else{
				groupAttendanceTO = new GroupAttendanceTO(studentAttendance.getGroupAttendance());
				groupAttendanceIdObjectMap.put(studentAttendance.getGroupAttendance().getId(),groupAttendanceTO);
			}
			
			dateAttendanceMap.put(groupAttendanceTO.getFromTimeObject().getTime(), studentAttendance);
			studentDateAttendanceMap.put(studentAttendance.getEmail(), dateAttendanceMap);
			/*
			 * Unique from time for an object
			 */
			attendanceDates.add(groupAttendanceTO.getFromTimeObject().getTime());
			
			if(!studentIdNameMap.containsKey(studentAttendance.getEmail())){
				studentIdNameMap.put(studentAttendance.getEmail(), studentAttendance.getName());
			}
		}
		
		Set<Long> sortedAttendanceDates = new TreeSet<>(attendanceDates);
		
		for(Long date: sortedAttendanceDates){
			String dateString = DateUtils.formatDate(new Date(date), DateUtils.DAY_MONTH_TIME_PATTERN);
			attendanceDatesString.put(date, dateString);
		}
		
		
	}
	
	public byte[] generateGroupAttendanceReport(long groupId) throws UnsupportedEncodingException {
		logger.info("start generateGroupAttendanceReport");
		StringBuffer sb =null;
		try {
						
			Map<Long, GroupAttendanceTO> groupAttendanceIdObjectMap = new HashMap<>();
			
			List<StudentAttendance> allStudentAttendanceForGroup = studentAttendenceService.getAllByGroupId(groupId);
			Map<Long, String> attendanceDatesString = new HashMap<>();
			Map<String, Map<Long,StudentAttendance>> studentDateAttendanceMap = new HashMap();
			Map<String,String> studentIdNameMap = new HashMap<>();
			
			createMapsForAttendanceReport(groupAttendanceIdObjectMap, allStudentAttendanceForGroup, attendanceDatesString, studentDateAttendanceMap, studentIdNameMap);
			
			String header = "Name,Email";
			
			for(Long date: attendanceDatesString.keySet()){
				header+=","+attendanceDatesString.get(date);
			}
			
			header += "\n";
			
			sb = new StringBuffer(header);
			
			for (String email : studentDateAttendanceMap.keySet()) {
				String name = studentIdNameMap.get(email);
				sb.append("\"" + name + "\",\"" + email +"\",");
				Map<Long,StudentAttendance> dateAttendanceStatusMap = studentDateAttendanceMap.get(email);
				
				for (Long date : attendanceDatesString.keySet()) {
					
					if(dateAttendanceStatusMap.containsKey(date)){
						StudentAttendance attendance = dateAttendanceStatusMap.get(date);
						sb.append("\"" + attendance.getStatus().getShortLable() + "\",");
					}else{
						sb.append("-,");
					}
				}
				sb.append("\n");
			}
			
		} catch (Exception e) {

			logger.error(e.getMessage(), e);
		}
		return sb.toString().getBytes(Constants.UTF_8);
	}
}
