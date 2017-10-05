package com.notes.nicefact.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="group_join_member")
public class GroupJoinMember extends CommonEntity {
	private static final long serialVersionUID = 1L;

	Long parentId;
	
	Long childId;

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public Long getChildId() {
		return childId;
	}

	public void setChildId(Long childId) {
		this.childId = childId;
	}
	
	
}
