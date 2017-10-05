package com.notes.nicefact.dao;

import javax.persistence.EntityManager;

import com.notes.nicefact.dao.impl.CommonDAOImpl;
import com.notes.nicefact.entity.PostReaction;

public class PostReactionDAO extends CommonDAOImpl<PostReaction> {
	public PostReactionDAO(EntityManager em) {
		super(em);
	}
}
