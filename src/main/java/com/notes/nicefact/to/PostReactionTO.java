package com.notes.nicefact.to;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.notes.nicefact.entity.AbstractPostReaction;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PostReactionTO implements Serializable{
	private static final long serialVersionUID = 1L;

	String email;

	String name;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PostReactionTO(AbstractPostReaction reaction) {
		super();
		this.email = reaction.getEmail();
		this.name = reaction.getName();
	}

	public PostReactionTO() {
		super();
	}

}
