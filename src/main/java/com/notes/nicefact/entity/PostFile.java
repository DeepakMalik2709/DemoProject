package com.notes.nicefact.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import com.notes.nicefact.to.FileTO;

@Entity
public class PostFile   extends AbstractFile{

	private static final long serialVersionUID = 1L;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Post post;
	
	
	public PostFile() {
		super();
	}

	public PostFile(FileTO file, String path) {
		super(file, path);
	}

	public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}
}
