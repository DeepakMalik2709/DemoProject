package com.notes.nicefact.entity;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import com.notes.nicefact.to.TagTO;

@Entity
public class Tag extends CommonEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String name;
	
	String nameUpperCase;
	
	String description;
	
	@OneToMany(mappedBy="tag", cascade = CascadeType.ALL)
	private Set<PostTag> postTags = new LinkedHashSet<>();
	
	public Tag() {
	}
	
	public Tag(TagTO tag) {
		this.name = tag.getName();
		this.description = tag.getDescription();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public Set<PostTag> getPostTags() {
		return postTags;
	}

	public void setPostTags(Set<PostTag> postTags) {
		this.postTags = postTags;
	}

	public void updateProps(Tag tag) {
		this.name = tag.getName();
		this.description = tag.getDescription();
	}
	
	
	@PrePersist
	@PreUpdate
	public void preStore() {
		nameUpperCase = name.toUpperCase().trim();
		super.preStore();
	}
}
