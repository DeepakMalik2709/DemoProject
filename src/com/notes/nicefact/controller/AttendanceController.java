package com.notes.nicefact.controller;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

import org.apache.log4j.Logger;

import com.notes.nicefact.entity.Group;
import com.notes.nicefact.service.GroupService;
import com.notes.nicefact.service.attendance.AttendanceService;
import com.notes.nicefact.util.EntityManagerHelper;

@Path("/attendance")
public class AttendanceController extends CommonController {

	private final static Logger logger = Logger.getLogger(AttendanceController.class.getName());

	@GET
	@Path("{groupId}/report/download")
	public void downlaodTaskSubmission(@PathParam("groupId") long groupId, @Context HttpServletRequest request, @Context HttpServletResponse response) {
		logger.info("downlaodAttendnanceReport start");
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			
			AttendanceService attendanceService = new AttendanceService(em);
			GroupService groupService = attendanceService.getGroupService();
			
			Group group = groupService.get(groupId);
			byte[] fileBytes = attendanceService.generateGroupAttendanceReport(groupId);
			if (null !=fileBytes) {
				downloadFile(fileBytes, group.getName()+" Attendance.csv", "Text/csv", response);
			}
			return;
		} catch (Exception e) {
			logger.error(e.getMessage(), e );

		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
		renderResponseRaw("downloaing failed", response);
		logger.info("downlaodAttendnanceReport exit");
	}

}
