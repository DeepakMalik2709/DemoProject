package com.notes.nicefact.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.notes.nicefact.enums.LANGUAGE;
import com.notes.nicefact.enums.SHARING;
import com.notes.nicefact.to.GroupTO;
import com.notes.nicefact.to.TagTO;

@Entity
@Table(name="Groups")
public class Group extends CommonEntity {

	private static final long serialVersionUID = 1L;

	// members in group
	@OneToMany(mappedBy = "group", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<GroupMember> members = new HashSet<>();
/*
	@JoinTable(name = "group_join_group", joinColumns = { @JoinColumn(name = "childId", referencedColumnName = "id", nullable = false) }, inverseJoinColumns = {
			@JoinColumn(name = "parentId", referencedColumnName = "id", nullable = false) })
	@ManyToMany
	private List<Group> groupMembers = new ArrayList<>();

	@ManyToMany(mappedBy = "groupMembers")
	private List<Group> groupParents = new ArrayList<>();*/
	
	@ElementCollection(fetch = FetchType.LAZY)
	Set<Long> memberGroupsIds = new HashSet<>();
	
	@Enumerated(EnumType.STRING)
	private SHARING sharing = SHARING.PRIVATE;

	// company key of person creating post
	@Basic
	private Long companyId;

	// name of group
	String name;

	@Basic
	private Integer noOfMembers;

	@Basic
	private Integer noOfAdmins;

	@Basic
	private Integer noOfPosts;

	@Basic
	private String folderId;

	@Basic
	private String bgImageId;

	@Basic
	private String icon;

	@ElementCollection(fetch = FetchType.EAGER)
	Set<String> admins = new HashSet<>();

	@ElementCollection(fetch = FetchType.EAGER)
	Set<String> blocked = new HashSet<>();
	
	@Transient
	Set<Tag> tags;
	
	@ElementCollection(fetch = FetchType.EAGER)
	Set<Long> tagIds = new HashSet<>();

	@ElementCollection(targetClass = LANGUAGE.class, fetch = FetchType.EAGER)
	@Enumerated(EnumType.STRING)
	private Set<LANGUAGE> languages = new HashSet<>();

	// description of group
	@Column(columnDefinition = "TEXT")
	String description;

	String taskFolderId;
	String postFolderId;
	
	String assignmentFolderId;
	
	
	

	public String getAssignmentFolderId() {
		return assignmentFolderId;
	}

	public void setAssignmentFolderId(String assignmentFolderId) {
		this.assignmentFolderId = assignmentFolderId;
	}

	public String getTaskFolderId() {
		return taskFolderId;
	}

	public void setTaskFolderId(String taskFolderId) {
		this.taskFolderId = taskFolderId;
	}

	public String getPostFolderId() {
		return postFolderId;
	}

	public void setPostFolderId(String postFolderId) {
		this.postFolderId = postFolderId;
	}



	public Set<GroupMember> getMembers() {
		return members;
	}

	public void setMembers(Set<GroupMember> members) {
		this.members = members;
	}

	public SHARING getSharing() {
		return sharing;
	}

	public void setSharing(SHARING sharing) {
		this.sharing = sharing;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getNoOfMembers() {
		return noOfMembers;
	}

	public void setNoOfMembers(Integer noOfMembers) {
		this.noOfMembers = noOfMembers;
	}

	public Integer getNoOfAdmins() {
		return noOfAdmins;
	}

	public void setNoOfAdmins(Integer noOfAdmins) {
		this.noOfAdmins = noOfAdmins;
	}

	public Integer getNoOfPosts() {
		return noOfPosts;
	}

	public void setNoOfPosts(Integer noOfPosts) {
		this.noOfPosts = noOfPosts;
	}

	public String getFolderId() {
		return folderId;
	}

	public void setFolderId(String folderId) {
		this.folderId = folderId;
	}

	public String getBgImageId() {
		return bgImageId;
	}

	public void setBgImageId(String bgImageId) {
		this.bgImageId = bgImageId;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Set<String> getAdmins() {
		if(null == admins){
			return new HashSet<>();
		}
		return admins;
	}

	public void setAdmins(Set<String> admins) {
		this.admins = admins;
	}

	public Set<String> getBlocked() {
		if(null == blocked){
			return new HashSet<>();
		}
		return blocked;
	}

	public void setBlocked(Set<String> blocked) {
		this.blocked = blocked;
	}

	public Set<Tag> getTags() {
		if(null == tags){
			tags= new HashSet<>();
		}
		return tags;
	}
	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}
	public Set<Long> getTagIds() {
		if(null == tagIds){
			tagIds = new HashSet<>();
		}
		return tagIds;
	}
	public void setTagIds(Set<Long> tagIds) {
		this.tagIds = tagIds;
	}
	public Set<LANGUAGE> getLanguages() {
		if(null == languages){
			return new HashSet<>();
		}
		return languages;
	}

	public void setLanguages(Set<LANGUAGE> languages) {
		this.languages = languages;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<Long> getMemberGroupsIds() {
		if(null == memberGroupsIds){
			memberGroupsIds = new HashSet<>();
		}
		return memberGroupsIds;
	}

	public void setMemberGroupsIds(Set<Long> memberGroupsIds) {
		this.memberGroupsIds = memberGroupsIds;
	}

	public Group() {
		super();
	}

	public Group(GroupTO group) {
		super();
		this.sharing = group.getSharing();
		this.name = group.getName();
		this.icon = group.getIcon();
		if(null !=  group.getLanguages()){
			this.languages = new HashSet<>( group.getLanguages());
		}
		this.description = group.getDescription();
		for (TagTO tagTO : group.getTags()) {
			if(tagTO.getId() !=null && tagTO.getId() > 0){
				this.tagIds.add(tagTO.getId());
			}
		}
	}
	
	public void updateProps(Group group){
		this.sharing = group.getSharing();
		this.name = group.getName();
		this.icon = group.getIcon();
		this.languages = new HashSet<>();
		if(null !=  group.getLanguages()){
			this.languages = new HashSet<>( group.getLanguages());
		}
		this.description = group.getDescription();
		this.tagIds = new HashSet<>(group.getTagIds());
	}
}
