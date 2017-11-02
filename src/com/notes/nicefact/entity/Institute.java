package com.notes.nicefact.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;

import com.notes.nicefact.enums.InstituteType;
import com.notes.nicefact.to.InstituteTO;

/**
 *  This will be the top most group an organisation can have, or we can call it home page of XYZ institute.
 *  
 * @author jkb
 *
 */

@Entity
public class Institute extends CommonEntity {

	private static final long serialVersionUID = 1L;
	
	@Basic
	String name;
	
	@Column(columnDefinition = "TEXT")
	String description;
	
	@Enumerated(EnumType.STRING)
	InstituteType type;
	
	@Basic
	private Integer noOfMembers;

	@Basic
	private Integer noOfAdmins;
	
	String bgImageId;

	@ElementCollection(fetch = FetchType.EAGER)
	Set<String> admins = new HashSet<>();
	

	@ElementCollection(fetch = FetchType.EAGER)
	Set<String> blocked = new HashSet<>();
	
	public Institute(){};
	
	public Institute(InstituteTO group) {
		super();
		this.name = group.getName();
		this.description = group.getDescription();
		this.type = group.getType();
	}
	
	public void updateProps(InstituteTO group){
		this.name = group.getName();
		this.description = group.getDescription();
		this.type = group.getType();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public InstituteType getType() {
		return type;
	}

	public void setType(InstituteType type) {
		this.type = type;
	}

	public Integer getNoOfMembers() {
		return noOfMembers;
	}

	public void setNoOfMembers(Integer noOfMembers) {
		this.noOfMembers = noOfMembers;
	}

	public Integer getNoOfAdmins() {
		return noOfAdmins;
	}

	public void setNoOfAdmins(Integer noOfAdmins) {
		this.noOfAdmins = noOfAdmins;
	}

	public Set<String> getAdmins() {
		return admins;
	}

	public void setAdmins(Set<String> admins) {
		this.admins = admins;
	}
	
	public Set<String> getBlocked() {
		if(null == blocked){
			return new HashSet<>();
		}
		return blocked;
	}

	public void setBlocked(Set<String> blocked) {
		this.blocked = blocked;
	}

	public String getBgImageId() {
		return bgImageId;
	}

	public void setBgImageId(String bgImageId) {
		this.bgImageId = bgImageId;
	}
}
