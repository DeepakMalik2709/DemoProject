package com.notes.nicefact.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import com.google.api.services.calendar.model.EventAttendee;
import com.notes.nicefact.dao.CommonDAO;
import com.notes.nicefact.dao.GroupDAO;
import com.notes.nicefact.dao.GroupMemberDAO;
import com.notes.nicefact.dao.TagDAO;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.Group;
import com.notes.nicefact.entity.GroupMember;
import com.notes.nicefact.entity.Tag;
import com.notes.nicefact.enums.LANGUAGE;
import com.notes.nicefact.exception.ServiceException;
import com.notes.nicefact.exception.UnauthorizedException;
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
	public GroupService(EntityManager em) {
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
			addMembersToNewGroup(groupTO, group, appUser);
			upsert(group);
			appUser.getGroupIds().add(group.getId());
		}
		backendTaskService.createSendGroupAddNotificationTask(group.getId());
		backendTaskService.createUpdateGroupMemberAccessPermissionsTask(group.getId());
		CacheUtils.addGroupToCache(group);
		return group;
	}

	private void addMembersToNewGroup(GroupTO groupTO, Group group, AppUser appUserTO) {
		GroupMember member = new GroupMember(appUserTO);
		member.setIsAdmin(true);
		member.setGroup(group);
		group.getMembers().add(member);
		group.getAdmins().add(appUserTO.getEmail());
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
			GroupMember member;
			for (GroupMemberTO memberTO : members) {
				if (Utils.isValidEmailAddress(memberTO.getEmail())) {
					AppUser userHr = CacheUtils.getAppUser(memberTO.getEmail());
					if (null == userHr) {
						member = new GroupMember(memberTO.getEmail(), memberTO.getName());
						member.setIsAppUser(false);
					} else {
						member = new GroupMember(userHr);
					}
					member.setGroup(group);
					if (group.getMembers().add(member)) {
						allNewMembers.add(memberTO.getEmail());
					}
				}
			}

			List<SelectBoxTO> groups = children.getGroups();
			for (SelectBoxTO child : groups) {
				group.getMemberGroupsIds().add(child.getId());
			}
			group.getMemberGroupsIds().remove(group.getId());

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
		List<GroupMember> members = groupMemberDAO.fetchGroupMembersByGroupId(groupId, searchTO);
		List<GroupMemberTO> memberTos = new ArrayList<>();
		GroupMemberTO memberTO;
		for (GroupMember groupMember : members) {
			memberTO = new GroupMemberTO(groupMember);
			memberTos.add(memberTO);
		}
		return memberTos;
	}

	public void deleteGroupMember(long groupId, long memberId, AppUser appUser) {
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



	public GroupMember toggleGroupAdmin(long groupId, long memberId, boolean isAdmin) {
		GroupMember member = groupMemberDAO.get(memberId);
		if (member != null) {
			Group group = member.getGroup();
			if (group.getAdmins().contains(CurrentContext.getEmail())) {
				if (!isAdmin && group.getAdmins().contains(member.getEmail()) && group.getAdmins().size() == 1) {
					throw new ServiceException("Cannot remove only admin");
				}
				member.setIsAdmin(isAdmin);
				member = groupMemberDAO.upsert(member);
				if (isAdmin) {
					group.getAdmins().add(member.getEmail());
				} else {
					group.getAdmins().remove(member.getEmail());
				}
				groupDao.upsert(group);
			} else {
				throw new UnauthorizedException("You cannot update this group");
			}
		}
		return member;
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
		}
	}
	
	public void updateGroupMember(GroupMember  member){
		groupMemberDAO.upsert(member);
	}

	public List<EventAttendee> fetchMemberEmailFromGroup(List<Group> groups, SearchTO searchTO) {
		 List<EventAttendee> members = groupMemberDAO.getMemberEmailFromGroup(groups,searchTO);		
		return members;
	}

}
