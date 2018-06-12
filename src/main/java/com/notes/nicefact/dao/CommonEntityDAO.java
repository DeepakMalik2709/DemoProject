package com.notes.nicefact.dao;

import javax.persistence.EntityManager;

import com.notes.nicefact.dao.impl.CommonDAOImpl;
import com.notes.nicefact.entity.CommonEntity;

public class CommonEntityDAO extends CommonDAOImpl<CommonEntity> {
	public CommonEntityDAO(EntityManager em) {
		super(em);
	}
}
