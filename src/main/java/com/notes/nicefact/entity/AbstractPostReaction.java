package com.notes.nicefact.entity;

import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;

/**
 * @author JKB
 *	abstract PostReaction class with required fields for PostReaction
 */
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@MappedSuperclass
public class AbstractPostReaction extends CommonEntity{

	public enum ReactType{
		GOOD
	}
	private static final long serialVersionUID = 1L;
	
	String email;
	
	String name;
	
	ReactType type = ReactType.GOOD;
	
	private Boolean isMainComment = false;
	
	public AbstractPostReaction(AppUser hr) {
		this.email = hr.getEmail();
		this.name = hr.getDisplayName();
	}
	
	public AbstractPostReaction(){}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}



	public Boolean getIsMainComment() {
		return isMainComment;
	}

	public void setIsMainComment(Boolean isMainComment) {
		this.isMainComment = isMainComment;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ReactType getType() {
		return type;
	}

	public void setType(ReactType type) {
		this.type = type;
	}

	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractPostReaction other = (AbstractPostReaction) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		return true;
	}
}
