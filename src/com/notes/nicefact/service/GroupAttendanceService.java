/**
 * 
 */
package com.notes.nicefact.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import com.notes.nicefact.dao.CommonDAO;
import com.notes.nicefact.dao.GroupAttendanceDao;
import com.notes.nicefact.dao.StudentAttendenceDao;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.Group;
import com.notes.nicefact.entity.GroupAttendance;
import com.notes.nicefact.entity.GroupMember;
import com.notes.nicefact.entity.StudentAttendance;
import com.notes.nicefact.exception.AppException;
import com.notes.nicefact.exception.UnauthorizedException;
import com.notes.nicefact.to.AttendanceMemberTO;
import com.notes.nicefact.to.GroupAttendanceTO;
import com.notes.nicefact.to.SearchTO;
import com.notes.nicefact.to.StudentAttendanceTO;
import com.notes.nicefact.util.CacheUtils;

/**
 * @author user
 *
 */
public class GroupAttendanceService extends CommonService<GroupAttendance> {
	private final static Logger logger = Logger.getLogger(GroupAttendanceService.class.getName());
	private GroupAttendanceDao groupAttendenceDao;
	
	StudentAttendenceDao studentAttendenceDao;
	AppUserService appUserService;
	
	BackendTaskService backendTaskService;
	EntityManager em ;
	public GroupAttendanceService(EntityManager em) {
		this.em = em;
		studentAttendenceDao = new StudentAttendenceDao(em);
		groupAttendenceDao = new GroupAttendanceDao(em);
		appUserService = new AppUserService(em);
		backendTaskService  = new BackendTaskService(em);
	}

	@Override
	protected CommonDAO<GroupAttendance> getDAO() {
		return groupAttendenceDao;
	}
	
	public boolean deleteAttendance(GroupAttendanceTO groupAttendanceTO, AppUser user) {
		Group group = CacheUtils.getGroup(groupAttendanceTO.getGroupId());
		if(group == null || !group.getTeachers().contains(user.getEmail())){
			throw new UnauthorizedException("You cannot mark attendance for this group");
		}
		
		if(groupAttendanceTO.getId() == null){
			throw new AppException("Attendance id is required.");
		}else{
			GroupAttendance groupAttendance = get(groupAttendanceTO.getId());
			studentAttendenceDao.removeAll(groupAttendance.getStudentAttendances());
			remove(groupAttendance);
		}
		return true;
	}
	public GroupAttendance upsert(GroupAttendanceTO groupAttendanceTO, AppUser user) {
		GroupAttendance groupAttendance ;
		Group group = CacheUtils.getGroup(groupAttendanceTO.getGroupId());
		if(group == null || !group.getTeachers().contains(user.getEmail())){
			throw new UnauthorizedException("You cannot mark attendance for this group");
		}
		if(groupAttendanceTO.getId() == null){
			groupAttendance =new GroupAttendance(groupAttendanceTO);
			groupAttendance.setGroup(group);
		}else{
			groupAttendance = get(groupAttendanceTO.getId());
			groupAttendance.updateProps(groupAttendanceTO);
			for(StudentAttendance sa : groupAttendance.getStudentAttendances()){
				sa.setGroupAttendance(null);
			}
			studentAttendenceDao.removeAll(groupAttendance.getStudentAttendances());
			groupAttendance.getStudentAttendances().clear();
		}
		
		if(null !=groupAttendance){
			super.upsert(groupAttendance);
			List<StudentAttendance> list = new ArrayList<>();
			StudentAttendance studentAttendance;
			for(AttendanceMemberTO member : groupAttendanceTO.getMembers()){
				studentAttendance = new StudentAttendance(member, groupAttendance);
				list.add(studentAttendance);
			}
			studentAttendenceDao.upsertAll(list);
			groupAttendance.getStudentAttendances().addAll(list);
			super.upsert(groupAttendance);
		}
		
		
		return groupAttendance;
	}
	
	public GroupAttendance get(Long id) {
		GroupAttendance GroupAttendence = super.get(id);
		return GroupAttendence;
	}

	public GroupAttendanceTO fetchStudentAttendance(SearchTO searchTO,long groupId, String email, Date fromDate, Date toDate) {
		GroupAttendanceTO attendance = null;		
		attendance = new GroupAttendanceTO();
		attendance.setDate(searchTO.getDate());
		attendance.setFromTime(searchTO.getFromTime());
		attendance.setGroupId(searchTO.getGroupId());
		List<AttendanceMemberTO> studentTOs = new ArrayList<>();
		AttendanceMemberTO studentTO;
		List<StudentAttendance> studentAttendances  =  studentAttendenceDao.fetchStudentAttendance(searchTO,groupId,email, fromDate, toDate);
		
		for (StudentAttendance studentAttendance : studentAttendances) {
			studentTO = new AttendanceMemberTO(studentAttendance);
			studentTOs.add(studentTO);
		}
		attendance.setMembers(studentTOs);
		return attendance;
		
		
	}

	

}
