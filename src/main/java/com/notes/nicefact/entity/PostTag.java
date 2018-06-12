package com.notes.nicefact.entity;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

@Entity
@Table(name="Post_Tag")
public class PostTag {

	@EmbeddedId
    private PostTagId id;
	
	@ManyToOne
	@MapsId("postId")
	private Post post;
	
	@ManyToOne
	@MapsId("tagId")
	private Tag tag;
	
	public PostTag() {}
	
	public PostTag(Post post, Tag tag) {
        this.post = post;
        this.tag = tag;
        this.id = new PostTagId(post.getId(), tag.getId());
    }

	public PostTagId getId() {
		return id;
	}

	public void setId(PostTagId id) {
		this.id = id;
	}

	public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		this.tag = tag;
	}
}
