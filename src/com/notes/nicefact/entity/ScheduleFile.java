package com.notes.nicefact.entity;

import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PreUpdate;

public class ScheduleFile  extends AbstractComment {

	private static final long serialVersionUID = 1L;
	@ManyToOne(fetch = FetchType.LAZY)
	private Schedule schedule;


	
	@PreUpdate
	void prePersist(){
		super.preStore();		
	}

}
