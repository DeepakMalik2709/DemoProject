package com.notes.nicefact.to;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.notes.nicefact.entity.Tag;

@JsonIgnoreProperties(ignoreUnknown=true)
public class TagTO implements Serializable{

	private static final long serialVersionUID = 1L;

	String name;
	
	String description;
	
	Long id;

	public TagTO() {
	}
	
	public TagTO(Tag tag) {
		this.id = tag.getId();
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

	public Long getId() {
		if(null == id){
			return -1l;
		}
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "TagTO [name=" + name + ", description=" + description + ", id=" + id + "]";
	}
	
}
