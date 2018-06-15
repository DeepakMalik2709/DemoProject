package com.notes.nicefact.entity;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Certificate extends CommonEntity {

	private static final long serialVersionUID = 3931039217123972605L;

	private String name;
	private String date;
	private String image;
	private String organisation;
	private String grade;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appUser_id")
	private AppUser appUser;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getOrganisation() {
		return organisation;
	}
	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	
	@ManyToOne
	public AppUser getAppUser() {
		return appUser;
	}
	public void setAppUser(AppUser appUser) {
		this.appUser = appUser;
	}
	
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("id", id);
		map.put("name", getName());
		map.put("date", getDate());
		map.put("image", getImage());
		map.put("organisation", getOrganisation());
		map.put("grade", getGrade());
		map.put("userId", getAppUser().getId());
		
		return map;
	}
}