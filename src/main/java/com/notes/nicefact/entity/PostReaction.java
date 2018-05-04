package com.notes.nicefact.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;


/**
 * @author JKB
 * PostReaction object database representation
 */
@Entity
public class PostReaction extends AbstractPostReaction{
	
	private static final long serialVersionUID = 1L;
	//Ancestor
	@ManyToOne(fetch = FetchType.LAZY)
	Post post;

	public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}
	public PostReaction() {
	}

	public PostReaction(AppUser hr) {
		super(hr);
	}
	
}
