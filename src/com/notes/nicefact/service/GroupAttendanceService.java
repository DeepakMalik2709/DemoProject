/**
 * 
 */
package com.notes.nicefact.service;

import java.util.List;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import com.notes.nicefact.dao.CommonDAO;
import com.notes.nicefact.dao.GroupAttendenceDao;
import com.notes.nicefact.dao.StudentAttendenceDao;
import com.notes.nicefact.entity.Group;
import com.notes.nicefact.entity.GroupAttendance;
import com.notes.nicefact.entity.StudentAttendence;
import com.notes.nicefact.to.SearchTO;

/**
 * @author user
 *
 */
public class GroupAttendanceService extends CommonService<GroupAttendance> {
	private final static Logger logger = Logger.getLogger(GroupAttendanceService.class.getName());
	private GroupAttendenceDao groupAttendenceDao;
	
	StudentAttendenceDao studentAttendenceDao;
	AppUserService appUserService;
	
	BackendTaskService backendTaskService;
	EntityManager em ;
	public GroupAttendanceService(EntityManager em) {
		this.em = em;
		studentAttendenceDao = new StudentAttendenceDao(em);
		groupAttendenceDao = new GroupAttendenceDao(em);
		appUserService = new AppUserService(em);
		backendTaskService  = new BackendTaskService(em);
	}

	@Override
	protected CommonDAO<GroupAttendance> getDAO() {
		return groupAttendenceDao;
	}

	public GroupAttendance upsert(GroupAttendance groupAttendence) {
		GroupAttendance db = super.upsert(groupAttendence);
		if(db.getId()!=null){
			groupAttendence.setStudentAttendences((List<StudentAttendence>) studentAttendenceDao.upsertAll(groupAttendence.getStudentAttendences()));
		}
		return db;
	}
	
	public GroupAttendance get(Long id) {
		GroupAttendance GroupAttendence = super.get(id);
		return GroupAttendence;
	}

	
	
	public List<GroupAttendance> fetchMyGroups(SearchTO searchTO, Group group) {
		
		List<GroupAttendance> list = groupAttendenceDao.getByGroup(group);
		
		
		return list;
	}

}
