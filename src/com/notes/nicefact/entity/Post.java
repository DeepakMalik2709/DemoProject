package com.notes.nicefact.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;

import com.notes.nicefact.entity.AbstractRecipient.RecipientType;
import com.notes.nicefact.enums.SHARING;
import com.notes.nicefact.enums.ScheduleAttendeeResponseType;
import com.notes.nicefact.to.PostRecipientTO;
import com.notes.nicefact.to.PostTO;
import com.notes.nicefact.util.CacheUtils;

@Entity
public class Post extends AbstractComment {

	private static final long serialVersionUID = 1L;
	
	public enum POST_TYPE{
		SIMPLE, SCHEDULE, TASK
	}
	
	public enum TASK_TYPE{
		CLASS_ASSIGNMENT
	}

	// company key of person creating post
	@Basic
	private Long companyId;

	@Basic
	private Long groupId;

	@Transient
	Set<Tag> tags;

	@ElementCollection(fetch = FetchType.LAZY)
	Set<String> accessList;
	
	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
	List<PostComment> comments = new ArrayList<>();

	int numberOfComments;

	int numberOfReactions;

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	protected List<PostRecipient> recipients = new ArrayList<>();

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	protected Set<PostReaction> reactions = new LinkedHashSet<>();
	
	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	protected List<PostFile> files = new ArrayList<>();
	
	@Enumerated(EnumType.STRING)
	private SHARING sharing = SHARING.GROUP;

	@Enumerated(EnumType.STRING)
	private POST_TYPE postType = POST_TYPE.SIMPLE;
	
	@ElementCollection(fetch = FetchType.LAZY)
	private List<String> weekdays ;
	
int noOfSubmissions;
	
	@Basic
	Date deadline;

	@Basic
	Date fromDate;
	
	@Basic
	Date toDate;
	
	@ElementCollection(fetch = FetchType.LAZY)
	Set<String> submitters = new HashSet<>();
	
	private String zipFilePath;
	
	
	@Basic
	Date zipFileDate;
	
	String googleDriveFolderId;
	
	String title;
	
	String location;
	String weekday;
	private String googleEventId;
	public String getGoogleEventId() {
		return googleEventId;
	}
	public void setGoogleEventId(String googleEventId) {
		this.googleEventId = googleEventId;
	}
	
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Post() {
		super();
	}

	public Post(PostTO post) {
		super();
		this.groupId = post.getGroupId();
		this.comment = post.getComment();
		PostRecipient recipient;
		for (PostRecipientTO postRecipientTO : post.getRecipients()) {
			recipient = new PostRecipient();
			recipient.setEmail(postRecipientTO.getEmail());
			recipient.setScheduleResponse(ScheduleAttendeeResponseType.valueOf(postRecipientTO.getScheduleResponse()) );
			recipient.setName(postRecipientTO.getLabel());
			recipient.setPost(this);
			AppUser hr = CacheUtils.getAppUser(postRecipientTO.getEmail());
			if (hr == null) {
				recipient.setType(RecipientType.EMAIL);
			} else {
				recipient.setType(RecipientType.USER);
				recipient.setName(hr.getDisplayName());
				recipient.setPosition(hr.getPosition());
				recipient.setDepartment(hr.getDepartment());
				recipient.setOrganization(hr.getOrganization());
			}
			this.recipients.add(recipient);
		}
		
		if(null != this.getGroupId()){
			this.sharing = SHARING.GROUP;
		}
		if(post.getDeadlineTime()  >0){
			this.deadline = new Date(post.getDeadlineTime());
		}
		if(post.getFromDate()    >0){
			this.fromDate =new Date(post.getFromDate());
		}
		if(post.getToDate()    >0){
			this.toDate = new Date(post.getToDate());
		}
		if(post.getPostType()!=null){
			this.postType=post.getPostType();
		}
		if(post.getLocation()!=null){
			this.location = post.getLocation();
		}
		
		this.title = post.getTitle();
		this.googleEventId = post.getGoogleEventId();
		this.weekday= post.getWeekDay();
	}

	public void updateProps(Post post) {
		this.tags = post.getTags();
		this.comment = post.getComment();
		this.isEdited=true;
		if(post.getDeadline()  !=null){
			this.deadline = new Date(post.getDeadline().getTime());
		}
		this.title = post.getTitle();
	}

	public Long getCompanyId() {
		return companyId;
	}

	public List<PostComment> getComments() {
		return comments;
	}

	public void setComments(List<PostComment> comments) {
		this.comments = comments;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}
	public Set<String> getAccessList() {
		if(null == accessList){
			this.accessList = new HashSet<>();
		}
		return accessList;
	}

	public void setAccessList(Set<String> accessList) {
		this.accessList = accessList;
	}

	public List<PostFile> getFiles() {
		if(null == files){
			files = new ArrayList<>();
		}
		return files;
	}

	public void setFiles(List<PostFile> files) {
		this.files = files;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public int getNumberOfComments() {
		return numberOfComments;
	}

	public void setNumberOfComments(int numberOfComments) {
		this.numberOfComments = numberOfComments;
	}

	public int getNumberOfReactions() {
		return numberOfReactions;
	}

	public void setNumberOfReactions(int numberOfReactions) {
		this.numberOfReactions = numberOfReactions;
	}

	public Set<Tag> getTags() {
		if(null == tags){
			tags= new LinkedHashSet<>();
		}
		return tags;
	}
	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}

	public List<PostRecipient> getRecipients() {
		if (null == recipients) {
			recipients = new ArrayList<>();
		}
		return recipients;
	}

	public SHARING getSharing() {
		return sharing;
	}

	public void setSharing(SHARING sharing) {
		this.sharing = sharing;
	}

	public void setRecipients(List<PostRecipient> recipients) {
		this.recipients = recipients;
	}


	public Set<PostReaction> getReactions() {
		if (null == reactions) {
			reactions = new LinkedHashSet<>();
		}
		return reactions;
	}

	public void setReactions(Set<PostReaction> reactions) {
		this.reactions = reactions;
	}
	
	public List<String> getRecipientEmails() {
		List<String> emails = new ArrayList<>();
		for(PostRecipient receipient : getRecipients()){
			emails.add(receipient.getEmail());
		}
		return emails;
	}

	public POST_TYPE getPostType() {
		return postType;
	}

	public void setPostType(POST_TYPE postType) {
		this.postType = postType;
	}

	public int getNoOfSubmissions() {
		return noOfSubmissions;
	}

	public void setNoOfSubmissions(int noOfSubmissions) {
		this.noOfSubmissions = noOfSubmissions;
	}

	public Date getDeadline() {
		return deadline;
	}

	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}

	public Set<String> getSubmitters() {
		if(null == submitters){
			submitters = new HashSet<>();
		}
		return submitters;
	}

	public void setSubmitters(Set<String> submitters) {
		this.submitters = submitters;
	}

	public String getZipFilePath() {
		return zipFilePath;
	}

	public void setZipFilePath(String zipFilePath) {
		this.zipFilePath = zipFilePath;
	}

	public String getGoogleDriveFolderId() {
		return googleDriveFolderId;
	}

	public void setGoogleDriveFolderId(String googleDriveFolderId) {
		this.googleDriveFolderId = googleDriveFolderId;
	}

	public Date getZipFileDate() {
		return zipFileDate;
	}

	public void setZipFileDate(Date zipFileDate) {
		this.zipFileDate = zipFileDate;
	}

	@PrePersist
	@PreUpdate
	void prePersist() {
		super.preStore();
		this.numberOfReactions = getReactions().size();
		this.numberOfComments = getComments().size();
		Set<String> accessSet = new HashSet<>();
		accessSet.add(this.getCreatedBy());
		accessSet.addAll(this.getRecipientEmails());
		for(PostComment comment1 : getComments()){
			accessSet.add(comment1.getCreatedBy());
			accessSet.addAll(comment1.getRecipientEmails());
			for(PostComment reply : comment1.getComments()){
				accessSet.add(reply.getCreatedBy());
				accessSet.addAll(reply.getRecipientEmails());
			}
		}
		if(null == accessList){
			accessList = new HashSet<>();
		}else{
			accessList.clear();
		}
		accessList.addAll(accessSet);
		
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	
	public List<String> getWeekdays() {
		if(null == weekdays){
			this.weekdays = new ArrayList<>();
		}
		return weekdays;
	}
	public void setWeekdays(List<String> weekdays) {
		this.weekdays = weekdays;
	}
}
