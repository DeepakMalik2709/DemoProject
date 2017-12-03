package com.notes.nicefact.dao;

import javax.persistence.EntityManager;

import com.notes.nicefact.dao.impl.CommonDAOImpl;
import com.notes.nicefact.entity.PostRecipient;

public class PostRecipientDAO extends CommonDAOImpl<PostRecipient> {
	public PostRecipientDAO(EntityManager em) {
		super(em);
	}
}
