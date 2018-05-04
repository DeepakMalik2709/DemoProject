package com.notes.nicefact.entity;

import javax.persistence.Entity;
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
