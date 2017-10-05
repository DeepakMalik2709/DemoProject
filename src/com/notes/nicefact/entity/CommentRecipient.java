package com.notes.nicefact.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@Entity
public class CommentRecipient extends AbstractRecipient {

	private static final long serialVersionUID = 1L;
	@ManyToOne(fetch = FetchType.LAZY)
	PostComment comment;

	public PostComment getComment() {
		return comment;
	}

	public void setComment(PostComment comment) {
		this.comment = comment;
	}

}