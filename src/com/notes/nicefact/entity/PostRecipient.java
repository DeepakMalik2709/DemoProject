package com.notes.nicefact.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Entity
public class PostRecipient extends AbstractRecipient {

	private static final long serialVersionUID = 1L;
	// Ancestor
	@ManyToOne(fetch = FetchType.LAZY)
	Post post;

	public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}

}