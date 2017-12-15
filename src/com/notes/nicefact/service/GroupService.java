package com.notes.nicefact.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.api.services.calendar.model.EventAttendee;
import com.notes.nicefact.dao.CommonDAO;
import com.notes.nicefact.dao.GroupDAO;
import com.notes.nicefact.dao.GroupMemberDAO;
import com.notes.nicefact.dao.TagDAO;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.Group;
import com.notes.nicefact.entity.GroupAttendance;
import com.notes.nicefact.entity.GroupMember;
import com.notes.nicefact.entity.Institute;
import com.notes.nicefact.entity.Notification;
import com.notes.nicefact.entity.NotificationRecipient;
import com.notes.nicefact.entity.Tag;
import com.notes.nicefact.enums.LANGUAGE;
import com.notes.nicefact.enums.NotificationAction;
import com.notes.nicefact.enums.NotificationType;
import com.notes.nicefact.enums.UserPosition;
import com.notes.nicefact.exception.ServiceException;
import com.notes.nicefact.exception.UnauthorizedException;
import com.notes.nicefact.to.AttendanceMemberTO;
import com.notes.nicefact.to.GroupAttendanceTO;
import com.notes.nicefact.to.GroupChildrenTO;
import com.notes.nicefact.to.GroupMemberTO;
import com.notes.nicefact.to.GroupTO;
import com.notes.nicefact.to.SearchTO;
import com.notes.nicefact.to.SelectBoxTO;
import com.notes.nicefact.util.CacheUtils;
import com.notes.nicefact.util.CurrentContext;
import com.notes.nicefact.util.Utils;

public class GroupService extends CommonService<Group> {
	private final static Logger logger = Logger.getLogger(GroupService.class.getName());
	private GroupDAO groupDao;
	private GroupMemberDAO groupMemberDAO;
	AppUserService appUserService;
	private TagDAO tagDao;
	BackendTaskService backendTaskService;
	EntityManager em ;
	public GroupService(EntityManager em) {
		this.em = em;
		groupDao = new GroupDAO(em);
		groupMemberDAO = new GroupMemberDAO(em);
		appUserService = new AppUserService(em);
		tagDao = new TagDAO(em);
		backendTaskService  = new BackendTaskService(em);
	}

	@Override
	protected CommonDAO<Group> getDAO() {
		return groupDao;
	}

	public Group upsert(Group group) {
		Group db = super.upsert(group);
		CacheUtils.addGroupToCache(db);
		return db;
	}
	
	public Group get(Long id) {
		Group group = super.get(id);
		if(null != group){
			populateTagsInGroup(group);
		}
		return group;
	}

	public void populateTagsInGroup(Group tutorial){
		List<Group> list = new ArrayList<>();
		list.add(tutorial);
		populateTagsInGroups(list);
	}
	
	public void populateTagsInGroups(List<Group> tutorials){
		Set<Long> tagIds = new HashSet<>();
		for (Group tutorial : tutorials) {
			tagIds.addAll(tutorial.getTagIds());
		}
		if(!tagIds.isEmpty()){
			List<Tag> tags =  tagDao.getByKeys(tagIds);
			for (Group group : tutorials) {
				Set<Tag> thisTags = new HashSet<>();
				for (Tag tag : tags) {
					if(group.getTagIds().contains(tag.getId())){
						thisTags.add(tag);
					}
				}
				group.setTags(thisTags);
			}
		}
	}
	public Group upsert(GroupTO groupTO, AppUser appUser) {
		Group group = new Group(groupTO);
		/* temporarily set all group language to english*/
		groupTO.getLanguages().add(LANGUAGE.ENGLISH);
		if (groupTO.getId() > 0) {
			Group tutorialDB = get(groupTO.getId());
			if (tutorialDB.getAdmins().contains(appUser.getEmail())) {
				tutorialDB.updateProps(group);
				upsert(tutorialDB);
				return tutorialDB;
			} else {
				throw new UnauthorizedException();
			}
		} else {
			if(groupTO.getInstituteId() !=null && groupTO.getInstituteId() > 0){
				InstituteService instituteService = new InstituteService(em);
				Institute institute = instituteService.get(groupTO.getInstituteId());
				group.setInstitute(institute);
			}
			addMembersToNewGroup(groupTO, group, appUser);
			upsert(group);
			appUser.getGroupIds().add(group.getId());
		}
		backendTaskService.createSendGroupAddNotificationTask(group.getId());
		backendTaskService.createUpdateGroupMemberAccessPermissionsTask(group.getId());
		return group;
	}

	private void addMembersToNewGroup(GroupTO groupTO, Group group, AppUser user) {
		GroupMember member = new GroupMember(user);
		member.getPositions().add(UserPosition.ADMIN);
		member.setIsJoinRequestApproved(true);
		member.setIsBlocked(false);
		member.setJoinRequestApproveDate(new Date());
		member.setJoinRequestApprover(user.getEmail());
		member.setGroup(group);
		group.getMembers().add(member);
		group.getAdmins().add(user.getEmail());
		if (groupTO.getMembers() != null) {
			for (GroupMemberTO memberTO : groupTO.getMembers()) {
				if (Utils.isValidEmailAddress(memberTO.getEmail())) {
					AppUser userHr = CacheUtils.getAppUser(memberTO.getEmail());
					if (null == userHr) {
						member = new GroupMember(memberTO.getEmail(), memberTO.getName());
						member.setIsAppUser(false);
					} else {
						member = new GroupMember(userHr);
					}
					member.setIsJoinRequestApproved(true);
					member.setIsBlocked(false);
					member.setJoinRequestApproveDate(new Date());
					member.setJoinRequestApprover(user.getEmail());
					member.getPositions().add(UserPosition.STUDENT);
					member.setGroup(group);
					group.getMembers().add(member);
				}
			}
		}
	}

	public GroupTO addGroupMembers(long groupId, GroupChildrenTO children, AppUser appuser) {
		List<GroupMemberTO> members = children.getMembers();
		GroupTO groupVO = null;
		Group group = groupDao.get(groupId);
		if (group.getAdmins().contains(appuser.getEmail())) {
			Set<String> allNewMembers = new HashSet<>();
			Set<GroupMember> allNewGroupMembers = new HashSet<>();
			GroupMember member;
			for (GroupMemberTO memberTO : members) {
				if (Utils.isValidEmailAddress(memberTO.getEmail())) {
					
					GroupMember dbmember = fetchGroupMemberByEmail(groupId, memberTO.getEmail());
					if (null == dbmember) {
						AppUser userHr = CacheUtils.getAppUser(memberTO.getEmail());
						if (null == userHr) {
							member = new GroupMember(memberTO.getEmail(), memberTO.getName());
							member.setIsAppUser(false);
						} else {
							member = new GroupMember(userHr);
						}
						if (memberTO.getPositions() == null || memberTO.getPositions().isEmpty()) {
							member.getPositions().add(UserPosition.STUDENT);
						} else {
							member.getPositions().addAll(memberTO.getPositions());
						}
						member.setGroup(group);
						member.setIsJoinRequestApproved(true);
						member.setIsBlocked(false);
						member.setJoinRequestApproveDate(new Date());
						member.setJoinRequestApprover(appuser.getEmail());
						allNewGroupMembers.add(member);
						group.getMembers().add(member);
						allNewMembers.add(member.getEmail());
					} else if (!dbmember.getIsJoinRequestApproved()) {
						dbmember.setIsJoinRequestApproved(true);
						dbmember.setIsBlocked(false);
						dbmember.setJoinRequestApproveDate(new Date());
						dbmember.setJoinRequestApprover(appuser.getEmail());
						allNewGroupMembers.add(dbmember);
					}
				}
			}

			List<SelectBoxTO> groups = children.getGroups();
			for (SelectBoxTO child : groups) {
				group.getMemberGroupsIds().add(child.getId());
			}
			group.getMemberGroupsIds().remove(group.getId());
			groupMemberDAO.upsertAll(allNewGroupMembers);
			group = upsert(group);
			
			groupVO = new GroupTO(group, false);

			for (GroupMember addedMember : group.getMembers()) {
				if (allNewMembers.contains(addedMember.getEmail())) {
					GroupMemberTO to = new GroupMemberTO(addedMember);
					groupVO.getMembers().add(to);
				}
			}

			backendTaskService.createSendGroupAddNotificationTask(groupId);
			backendTaskService.createUpdateGroupMemberAccessPermissionsTask(groupId);
		} else {
			throw new UnauthorizedException(appuser.getEmail() + " does not have permission to edit this group.");
		}

		return groupVO;
	}

	public List<GroupTO> fetchMyGroups(SearchTO searchTO, AppUser appUser) {
		List<GroupTO> groupTOs = new ArrayList<>();
		List<Group> list = groupDao.fetchMyGroups(searchTO, appUser);
		GroupTO groupTO;
		for (Group group : list) {
			groupTO = new GroupTO(group, false);
			groupTOs.add(groupTO);
		}
		return groupTOs;
	}

	public List<GroupMemberTO> fetchGroupMembers(long groupId, SearchTO searchTO) {
		List<GroupMember> members = groupMemberDAO.fetchByGroupId(groupId,true, searchTO);
		List<GroupMemberTO> memberTos = new ArrayList<>();
		GroupMemberTO memberTO;
		for (GroupMember groupMember : members) {
			memberTO = new GroupMemberTO(groupMember);
			memberTos.add(memberTO);
			if(groupMember.getPositions().isEmpty()){
				if(groupMember.getIsAdmin()){
					memberTO.getPositions().add(UserPosition.ADMIN);
				}else{
					memberTO.getPositions().add(UserPosition.STUDENT);
				}
			}
		}
		return memberTos;
	}

	public GroupAttendanceTO fetchGroupAttendanceMembers(SearchTO searchTO) {
		GroupAttendanceTO attendance = null;
		StudentAttendenceService attendenceService = new StudentAttendenceService(em);
		GroupAttendance groupAttendance = attendenceService.getGroupAttendance(searchTO);
		if(null == groupAttendance){
			attendance = new GroupAttendanceTO();
			attendance.setDate(searchTO.getDate());
			attendance.setFromTime(searchTO.getFromTime());
			attendance.setGroupId(searchTO.getGroupId());
			List<AttendanceMemberTO> memberTos = new ArrayList<>();
			AttendanceMemberTO memberTO;
			List<GroupMember> members = groupMemberDAO.fetchGroupAttendanceMembers( searchTO);
			for (GroupMember groupMember : members) {
				memberTO = new AttendanceMemberTO(groupMember);
				memberTos.add(memberTO);
			}
			attendance.setMembers(memberTos);
		}else{
			attendance = new GroupAttendanceTO(groupAttendance);
			attendance.setGroupId(searchTO.getGroupId());
		}
		addMissingNamesToAttendance(attendance);
		return attendance;
	}
	
public void addMissingNamesToAttendance(GroupAttendanceTO attendance ){
		for(AttendanceMemberTO sa : attendance.getMembers()){
			if(StringUtils.isBlank(sa.getName())){
				AppUser user = CacheUtils.getAppUser(sa.getEmail());
				if (null==user) {
					sa.setName("No name");
				}else{
					sa.setName(user.getDisplayName());
				}
			}
		}
	}
public GroupAttendanceTO fetchStudentAttendance(SearchTO searchTO,long groupId, long studentId) {
		GroupAttendanceTO attendance = null;
		StudentAttendenceService attendenceService = new StudentAttendenceService(em);
		GroupAttendance groupAttendance = attendenceService.getGroupAttendance(searchTO);
		if(null == groupAttendance){
			attendance = new GroupAttendanceTO();
			attendance.setDate(searchTO.getDate());
			attendance.setFromTime(searchTO.getFromTime());
			attendance.setGroupId(searchTO.getGroupId());
			List<AttendanceMemberTO> memberTos = new ArrayList<>();
			AttendanceMemberTO memberTO;
			List<GroupMember> members = groupMemberDAO.fetchGroupAttendanceMembers( searchTO);
			for (GroupMember groupMember : members) {
				memberTO = new AttendanceMemberTO(groupMember);
				memberTos.add(memberTO);
			}
			attendance.setMembers(memberTos);
		}else{
			attendance = new GroupAttendanceTO(groupAttendance);
			attendance.setGroupId(searchTO.getGroupId());
		}
		return attendance;
	}	public void deleteGroupMember(long groupId, long memberId, AppUser appUser) {
		GroupMember member = groupMemberDAO.get(memberId);
		if (member != null) {
			Group group = member.getGroup();
			if (group.getAdmins().contains(appUser.getEmail())) {
				groupMemberDAO.remove(memberId);
				udpateAppUserAccesPermissions(member.getEmail());
			} else {
				throw new UnauthorizedException("You cannot update this group");
			}
		}
	}


	public GroupMemberTO updateGroupMember(long groupId, GroupMemberTO memberTO, AppUser appuser) {
		GroupMemberTO updatedMember = null;
		Group group = get(groupId);
		if (group !=null && group.getAdmins().contains(appuser.getEmail())) {
			GroupMember dbmember = groupMemberDAO.get(memberTO.getId());
			if (null != dbmember) {
				if (memberTO.getPositions() == null || memberTO.getPositions().isEmpty()) {
					dbmember.getPositions().add(UserPosition.STUDENT);
				} else {
					dbmember.getPositions().clear();
					dbmember.getPositions().addAll(memberTO.getPositions());
				}
				boolean isAdmin = memberTO.getPositions().contains(UserPosition.ADMIN);
				boolean isTeacher = memberTO.getPositions().contains(UserPosition.TEACHER);
				if (isAdmin) {
					group.getAdmins().add(memberTO.getEmail());
				} else {
					group.getAdmins().remove(memberTO.getEmail());
				}
				
				if(isTeacher){
					group.getTeachers().add(memberTO.getEmail());
				}else{
					group.getTeachers().remove(memberTO.getEmail());
				}
			}
			
			group = upsert(group);
			groupMemberDAO.upsert(dbmember);
			updatedMember = new GroupMemberTO(dbmember);
		} else {
			throw new UnauthorizedException(appuser.getEmail() + " does not have permission to edit this group.");
		}

		return updatedMember;
	}


	public GroupMember toggleGroupBlock(long groupId, long memberId, boolean isBlocked) {
		GroupMember member = groupMemberDAO.get(memberId);
		if (member != null) {
			Group group = member.getGroup();
			if (group.getAdmins().contains(CurrentContext.getEmail())) {
				member.setIsBlocked(isBlocked);
				member = groupMemberDAO.upsert(member);
				if (isBlocked) {
					group.getBlocked().add(member.getEmail());
				} else {
					group.getBlocked().remove(member.getEmail());
				}
				groupDao.upsert(group);
			} else {
				throw new UnauthorizedException("You cannot update this group");
			}
		}
		return member;
	}

	public List<GroupTO> fetchMemberGroups(long groupId) {
		List<GroupTO> toList = new ArrayList<>();
		Group group = CacheUtils.getGroup(groupId);
		List<Group> list = groupDao.fetchGroupsbyIds(group.getMemberGroupsIds());
		GroupTO groupTO;
		for (Group group2 : list) {
			groupTO = new GroupTO(group2, false);
			toList.add(groupTO);
		}
		return toList;
	}

	public void deleteGroupMemberGroup(long groupId, long memberGroupId, AppUser appuser) {
		Group group = get(groupId);
		if (group != null) {
			if (group.getAdmins().contains(appuser.getEmail())) {
				group.getMemberGroupsIds().remove(memberGroupId);
				upsert(group);
				backendTaskService.createUpdateGroupMemberAccessPermissionsTask(groupId);
			} else {
				throw new UnauthorizedException();
			}
		}
	}

	public void updateGroupMemberAccessPermissions(Group group) {
		 Set<Long> processedIds =	new HashSet<Long>();
		 Set<String> processedEmails =	new HashSet<String>();
		 processedIds.add(group.getId());
		updateGroupMemberAccessPermissions(group, processedIds, processedEmails);
	}
	
	private void updateGroupMemberAccessPermissions(Group group , Set<Long> processedIds,  Set<String> processedEmails ) {
		for (GroupMember member : group.getMembers()) {
			if(processedEmails.add(member.getEmail())){
				udpateAppUserAccesPermissions(member.getEmail());
			}
		}
		for(Long childId : group.getMemberGroupsIds()){
			if(processedIds.add(childId)){
				updateGroupMemberAccessPermissions(CacheUtils.getGroup(childId), processedIds , processedEmails);
			}
		}
	}
	
	public void udpateAppUserAccesPermissions(String email){
		AppUser appUser = appUserService.getAppUserByEmail(email);
		if (null != appUser) {
			Set<Long> finalGroupIds = new HashSet<>();
			List<Long> groupIds = groupMemberDAO.fetchGroupMembersByEmail(email);
			List<Long> children = null;
			Long parentId;
			for (int i =0;i<groupIds.size();i++ ) {
				parentId = groupIds.get(i);
				if(finalGroupIds.add(parentId)){
					children = groupDao.getParentGroupsIds(parentId);
					if(null !=children && !children.isEmpty()){
						groupIds.addAll(children);
					}
				}
			}
			appUser.setGroupIds(finalGroupIds);
			appUserService.upsert(appUser);
			
			
			List<GroupMember> results =  groupMemberDAO.fetchAllGroupMembersByEmail(email);
			for (GroupMember member : results) {
				if(StringUtils.isBlank(member.getName())){
					member.setName(appUser.getDisplayName());
					groupMemberDAO.upsert(member);
				}
			}
		}
		
		
	}
	
	public void updateGroupMember(GroupMember  member){
		groupMemberDAO.upsert(member);
	}

	public List<EventAttendee> fetchMemberEmailFromGroup(List<Group> groups, SearchTO searchTO) {
		 List<EventAttendee> members = groupMemberDAO.getMemberEmailFromGroup(groups,searchTO);		
		return members;
	}

	public boolean isUserTeacher(Long groupId, String email) {
		GroupMember member = groupMemberDAO.fetchGroupMemberByEmail(groupId, email);
		if(null !=member && !member.getIsBlocked()){
			return member.getPositions().contains(UserPosition.TEACHER);
		}
		return false;
	}
public GroupMember fetchGroupMemberByEmail(long groupId, String email) {
		return groupMemberDAO.fetchGroupMemberByEmail(groupId, email);
	}
	
	public GroupMember joinGroup(long groupId, AppUser user) {
		GroupMember member =  fetchGroupMemberByEmail(groupId, user.getEmail());
		if (member == null) {
			Group group = get(groupId);
			if(group == null){
				throw new ServiceException("Group not found for id : " + groupId);
			}
			member = new GroupMember(user);
			member.setIsJoinRequestApproved(false);
			member.setIsBlocked(true);
			member.setGroup(group);
			groupMemberDAO.upsert(member);
			AppUser dbUser = appUserService.getAppUserByEmail(user.getEmail());
			dbUser.getJoinRequestGroups().add(groupId);
			appUserService.upsert(dbUser);
			
			NotificationService notificationService = new NotificationService(em);
			Notification notification = new Notification(dbUser);
			notification.setGroupId(group.getId()).setGroupName(group.getName()).setTitle(group.getName()).setType(NotificationType.GROUP);
			notificationService.upsert(notification);
			NotificationRecipient notificationRecipient;
			for (String email : group.getAdmins()) {
				AppUser admin = CacheUtils.getAppUser(email);
				if (admin == null) {
					notificationRecipient = new NotificationRecipient(email);
				} else {
					notificationRecipient = new NotificationRecipient(admin);
					notificationRecipient.setSendEmail(false);
				}
				notificationRecipient.setAction(NotificationAction.GROUP_JOIN_REQUESTED).setNotification(notification);
				notification.getRecipients().add(notificationRecipient);
				notificationService.upsertRecipient(notificationRecipient);
			}
			notificationService.upsert(notification);
		}
		return member;
	}
	
	public GroupMember approveJoinGroup(long groupId, GroupMemberTO memberTO, AppUser user) {
		GroupMember  member =null;
		Group group = CacheUtils.getGroup(groupId);
		if (group != null && group.getAdmins().contains(user.getEmail())) {
			member = fetchGroupMemberByEmail(groupId, memberTO.getEmail());
			if (member != null && !member.getIsJoinRequestApproved()) {
				member.setIsJoinRequestApproved(true);
				member.setIsBlocked(false);
				member.setJoinRequestApproveDate(new Date());
				member.setJoinRequestApprover(user.getEmail());
				if (memberTO.getPositions() == null || memberTO.getPositions().isEmpty()) {
					member.getPositions().add(UserPosition.STUDENT);
				} else {
					member.getPositions().clear();
					member.getPositions().addAll(memberTO.getPositions());
				}
				groupMemberDAO.upsert(member);
				AppUser dbUser = appUserService.getAppUserByEmail(memberTO.getEmail());
				dbUser.getJoinRequestGroups().remove(groupId);
				dbUser.getGroupIds().add(groupId);
				appUserService.upsert(dbUser);
				
				NotificationService notificationService = new NotificationService(em);
				Notification notification = new Notification(user);
				notification.setGroupId(group.getId()).setGroupName(group.getName()).setTitle(group.getName()).setType(NotificationType.GROUP);
				notificationService.upsert(notification);
				NotificationRecipient notificationRecipient = new NotificationRecipient(dbUser);
				notificationRecipient.setAction(NotificationAction.GROUP_JOIN_APPROVED).setNotification(notification);
				notification.getRecipients().add(notificationRecipient);
				notificationService.upsertRecipient(notificationRecipient);
				notificationService.upsert(notification);
			}
		}else{
			throw new UnauthorizedException();
		}
		return member;
	}
	
	public List<GroupMemberTO> fetchGroupJoinRequests(long groupId, SearchTO searchTO) {
		List<GroupMember> members = groupMemberDAO.fetchByGroupId(groupId,false, searchTO);
		List<GroupMemberTO> memberTos = new ArrayList<>();
		GroupMemberTO memberTO;
		for (GroupMember instituteMember : members) {
			memberTO = new GroupMemberTO(instituteMember);
			memberTos.add(memberTO);
		}
		return memberTos;
	}
	public List<Group> fetchGroupChildren(long groupId, SearchTO searchTO) {
		return groupDao.fetchGroupChildren(groupId, searchTO);
	}

	public List<Group> fetchInstituteChildren(long instituteId, SearchTO searchTO) {
		return groupDao.fetchInstituteChildren(instituteId, searchTO);
	}
}
