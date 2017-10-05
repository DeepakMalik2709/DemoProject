package com.notes.nicefact.dao;

import org.apache.log4j.Logger;

import javax.persistence.EntityManager;

import com.notes.nicefact.dao.impl.CommonDAOImpl;
import com.notes.nicefact.entity.PostComment;

public class PostCommentDAO extends CommonDAOImpl<PostComment> {
	static Logger logger = Logger.getLogger(PostCommentDAO.class.getSimpleName());

	public PostCommentDAO(EntityManager em) {
		super(em);
	}

}
