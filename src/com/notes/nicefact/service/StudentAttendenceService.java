package com.notes.nicefact.service;

import java.util.List;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import com.notes.nicefact.dao.CommonDAO;
import com.notes.nicefact.dao.StudentAttendenceDao;
import com.notes.nicefact.entity.GroupAttendence;
import com.notes.nicefact.entity.StudentAttendence;
import com.notes.nicefact.to.SearchTO;

public class StudentAttendenceService  extends CommonService<StudentAttendence> {
	private final static Logger logger = Logger.getLogger(GroupService.class.getName());
	private StudentAttendenceDao studentAttendenceDao;
	
	AppUserService appUserService;
	
	BackendTaskService backendTaskService;
	EntityManager em ;
	public StudentAttendenceService(EntityManager em) {
		this.em = em;
		studentAttendenceDao = new StudentAttendenceDao(em);
		appUserService = new AppUserService(em);
		backendTaskService  = new BackendTaskService(em);
	}

	@Override
	protected CommonDAO<StudentAttendence> getDAO() {
		return studentAttendenceDao;
	}

	public StudentAttendence upsert(StudentAttendence StudentAttendence) {
		StudentAttendence db = super.upsert(StudentAttendence);
		return db;
	}
	
	public StudentAttendence get(Long id) {
		StudentAttendence StudentAttendence = super.get(id);
		return StudentAttendence;
	}

	
	
	public List<StudentAttendence> fetchMyGroups(SearchTO searchTO, GroupAttendence groupAttendence) {
		
		List<StudentAttendence> list = studentAttendenceDao.getByGroupAttendence(groupAttendence);
		
		
		return list;
	}
}
