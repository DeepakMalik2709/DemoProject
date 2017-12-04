package com.notes.nicefact.service;

import java.util.List;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import com.notes.nicefact.dao.CommonDAO;
import com.notes.nicefact.dao.GroupAttendanceDao;
import com.notes.nicefact.dao.StudentAttendenceDao;
import com.notes.nicefact.entity.GroupAttendance;
import com.notes.nicefact.entity.StudentAttendance;
import com.notes.nicefact.to.SearchTO;

public class StudentAttendenceService  extends CommonService<StudentAttendance> {
	private final static Logger logger = Logger.getLogger(GroupService.class.getName());
	private StudentAttendenceDao studentAttendenceDao;
	GroupAttendanceDao groupAttendenceDao;
	AppUserService appUserService;
	
	BackendTaskService backendTaskService;
	EntityManager em ;
	public StudentAttendenceService(EntityManager em) {
		this.em = em;
		studentAttendenceDao = new StudentAttendenceDao(em);
		appUserService = new AppUserService(em);
		backendTaskService  = new BackendTaskService(em);
		groupAttendenceDao = new GroupAttendanceDao(em);
	}

	@Override
	protected CommonDAO<StudentAttendance> getDAO() {
		return studentAttendenceDao;
	}

	public StudentAttendance upsert(StudentAttendance StudentAttendence) {
		StudentAttendance db = super.upsert(StudentAttendence);
		return db;
	}
	
	public StudentAttendance get(Long id) {
		StudentAttendance StudentAttendence = super.get(id);
		return StudentAttendence;
	}

	
	
	public List<StudentAttendance> fetchMyGroups(SearchTO searchTO, GroupAttendance groupAttendence) {
		
		List<StudentAttendance> list = studentAttendenceDao.getByGroupAttendence(groupAttendence);
		
		
		return list;
	}

	public GroupAttendance getGroupAttendance(SearchTO searchTO) {
		GroupAttendance groupAttendance = groupAttendenceDao.getByGroupDate(searchTO);
		return groupAttendance;
	}
}
