package com.notes.nicefact.service;

import java.util.List;

import javax.persistence.EntityManager;

import com.notes.nicefact.dao.PostTagDao;
import com.notes.nicefact.entity.Post;
import com.notes.nicefact.entity.PostTag;
import com.notes.nicefact.entity.PostTagId;
import com.notes.nicefact.entity.Tag;

public class PostTagService {

	private PostTagDao postTagDao;
	
	public PostTagService(EntityManager em) {
		postTagDao = new PostTagDao(em);
	}
	
	public PostTag save(Post post, Tag tag) {
		return postTagDao.save(new PostTag(post, tag));
	}

	public List<PostTag> getByPostId(Long postId) {
		return postTagDao.getByPostId(postId);
	}
	
	public boolean remove(PostTag postTag) {
		return postTagDao.remove(postTag);
	}
	
	public boolean removeAll(List<PostTag> postTags) {
		return postTagDao.removeAll(postTags);
	}
}
