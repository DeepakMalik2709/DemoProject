package com.notes.nicefact.entity;

import java.util.ArrayList;
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
import com.notes.nicefact.to.PostRecipientTO;
import com.notes.nicefact.to.PostTO;
import com.notes.nicefact.util.CacheUtils;

@Entity
public class Post extends AbstractComment {

	private static final long serialVersionUID = 1L;

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
	}

	public void updateProps(Post post) {
		this.tags = post.getTags();
		this.comment = post.getComment();
		this.isEdited=true;
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
}
