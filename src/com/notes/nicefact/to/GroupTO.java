package com.notes.nicefact.to;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.notes.nicefact.entity.Group;
import com.notes.nicefact.entity.GroupMember;
import com.notes.nicefact.entity.Tag;
import com.notes.nicefact.enums.LANGUAGE;
import com.notes.nicefact.enums.SHARING;
import com.notes.nicefact.util.CurrentContext;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupTO {

	List<GroupMemberTO> members = new ArrayList<>();

	List<TagTO> tags = new ArrayList<>();
	private SHARING sharing;

	private Integer noOfPosts;

	Integer noOfMembers;

	Integer noOfAdmins;

	private String name;

	private String icon;

	private long id;
	
	private Long instituteId;
	
	private String instituteName;

	boolean isAdmin;
	boolean isMember;
	boolean isBlocked;
	
	boolean isGroupAttendaceAllowed;
	
	boolean isTeacher;
	boolean canMarkAttendance;

	private String bgImageId;

	String folderId;

	long lastModified;

	long created;

	String description;

	List<LANGUAGE> languages = new ArrayList<>();

	Set<String> admins = new HashSet<>();

	public GroupTO() {
	}

	public GroupTO(Group group, boolean fetchMembers) {
		this.id = group.getId();
		this.sharing = group.getSharing();
		this.noOfPosts = group.getNoOfPosts();
		this.noOfMembers = group.getNoOfMembers();
		this.noOfAdmins = group.getNoOfAdmins();
		this.name = group.getName();
		this.icon = group.getIcon();
		this.bgImageId = group.getBgImageId();
		this.folderId = group.getFolderId();
		this.lastModified = group.getUpdatedTime().getTime();
		this.created = group.getCreatedTime().getTime();
		this.languages.addAll(group.getLanguages());
		this.description = group.getDescription();
		admins = group.getAdmins();
		this.isGroupAttendaceAllowed = group.getIsGroupAttendaceAllowed();
		TagTO tagTO;
		for (Tag tag : group.getTags()) {
			tagTO = new TagTO(tag);
			this.tags.add(tagTO);
		}
		if (fetchMembers) {
			for (GroupMember member : group.getMembers()) {
				GroupMemberTO postReactionTO = new GroupMemberTO(member);
				members.add(postReactionTO);
			}
		}
		if(group.getInstitute() !=null){
			this.instituteId = group.getInstitute().getId();
			this.instituteName = group.getInstitute().getName();
		}
		if (CurrentContext.getAppUser() != null) {
			isMember = CurrentContext.getAppUser().getGroupIds().contains(id);
			isAdmin = group.getAdmins().contains(CurrentContext.getEmail());
			isBlocked = group.getBlocked().contains(CurrentContext.getEmail());
		}
	}
	public boolean getIsGroupAttendaceAllowed() {
		return isGroupAttendaceAllowed;
	}

	public void setIsGroupAttendaceAllowed(boolean isGroupAttendaceAllowed) {
		this.isGroupAttendaceAllowed = isGroupAttendaceAllowed;
	}
	
	public boolean getIsBlocked() {
		return isBlocked;
	}

	public void setIsBlocked(boolean isBlocked) {
		this.isBlocked = isBlocked;
	}

	public Integer getNoOfAdmins() {
		return noOfAdmins;
	}

	public void setNoOfAdmins(Integer noOfAdmins) {
		this.noOfAdmins = noOfAdmins;
	}

	public Integer getNoOfMembers() {
		return noOfMembers;
	}

	public void setNoOfMembers(Integer noOfMembers) {
		this.noOfMembers = noOfMembers;
	}

	public List<TagTO> getTags() {
		return tags;
	}

	public void setTags(List<TagTO> tags) {
		this.tags = tags;
	}

	public boolean getIsAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public String getFolderId() {
		return folderId;
	}

	public void setFolderId(String folderId) {
		this.folderId = folderId;
	}

	public Set<String> getAdmins() {
		return admins;
	}

	public void setAdmins(Set<String> admins) {
		this.admins = admins;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public List<LANGUAGE> getLanguages() {
		return languages;
	}

	public void setLanguages(List<LANGUAGE> languages) {
		this.languages = languages;
	}

	public long getCreated() {
		return created;
	}

	public void setCreated(long created) {
		this.created = created;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getLastModified() {
		return lastModified;
	}

	public void setLastModified(long lastModified) {
		this.lastModified = lastModified;
	}

	public SHARING getSharing() {
		return sharing;
	}

	public void setSharing(SHARING sharing) {
		this.sharing = sharing;
	}

	public Integer getNoOfPosts() {
		return noOfPosts;
	}

	public void setNoOfPosts(Integer noOfPosts) {
		this.noOfPosts = noOfPosts;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getBgImageId() {
		return bgImageId;
	}

	public void setBgImageId(String bgImageId) {
		this.bgImageId = bgImageId;
	}

	public boolean getIsMember() {
		return isMember;
	}

	public void setIsMember(boolean isMember) {
		this.isMember = isMember;
	}

	public List<GroupMemberTO> getMembers() {
		return members;
	}

	public void setMembers(List<GroupMemberTO> members) {
		this.members = members;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getInstituteId() {
		return instituteId;
	}

	public void setInstituteId(Long instituteId) {
		this.instituteId = instituteId;
	}

	public String getInstituteName() {
		return instituteName;
	}

	public void setInstituteName(String instituteName) {
		this.instituteName = instituteName;
	}

	public boolean getIsTeacher() {
		return isTeacher;
	}

	public void setIsTeacher(boolean isTeacher) {
		this.isTeacher = isTeacher;
	}

	public boolean getCanMarkAttendance() {
		return canMarkAttendance;
	}

	public void setCanMarkAttendance(boolean canMarkAttendance) {
		this.canMarkAttendance = canMarkAttendance;
	}

}
