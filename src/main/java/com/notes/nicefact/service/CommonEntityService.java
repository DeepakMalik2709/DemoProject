package com.notes.nicefact.service;

import javax.persistence.EntityManager;

import com.notes.nicefact.dao.CommonDAO;
import com.notes.nicefact.dao.CommonEntityDAO;
import com.notes.nicefact.entity.CommonEntity;

public class CommonEntityService extends CommonService<CommonEntity> {

	CommonEntityDAO  commonEntityDAO;
	
	public CommonEntityService(EntityManager em) {
		commonEntityDAO = new CommonEntityDAO(em);
	}
	
	@Override
	protected CommonDAO<CommonEntity> getDAO() {
		return commonEntityDAO;
	}

}
