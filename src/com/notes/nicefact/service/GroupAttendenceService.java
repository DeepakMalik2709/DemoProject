/**
 * 
 */
package com.notes.nicefact.service;

import java.util.List;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import com.notes.nicefact.dao.CommonDAO;
import com.notes.nicefact.dao.GroupAttendenceDao;
import com.notes.nicefact.entity.Group;
import com.notes.nicefact.entity.GroupAttendence;
import com.notes.nicefact.to.SearchTO;

/**
 * @author user
 *
 */
public class GroupAttendenceService extends CommonService<GroupAttendence> {
	private final static Logger logger = Logger.getLogger(GroupService.class.getName());
	private GroupAttendenceDao groupAttendenceDao;
	
	AppUserService appUserService;
	
	BackendTaskService backendTaskService;
	EntityManager em ;
	public GroupAttendenceService(EntityManager em) {
		this.em = em;
		groupAttendenceDao = new GroupAttendenceDao(em);
		appUserService = new AppUserService(em);
		backendTaskService  = new BackendTaskService(em);
	}

	@Override
	protected CommonDAO<GroupAttendence> getDAO() {
		return groupAttendenceDao;
	}

	public GroupAttendence upsert(GroupAttendence GroupAttendence) {
		GroupAttendence db = super.upsert(GroupAttendence);
		return db;
	}
	
	public GroupAttendence get(Long id) {
		GroupAttendence GroupAttendence = super.get(id);
		return GroupAttendence;
	}

	
	
	public List<GroupAttendence> fetchMyGroups(SearchTO searchTO, Group group) {
		
		List<GroupAttendence> list = groupAttendenceDao.getByGroup(group);
		
		
		return list;
	}
}
