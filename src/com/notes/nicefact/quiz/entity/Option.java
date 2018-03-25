package com.notes.nicefact.quiz.entity;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.notes.nicefact.entity.CommonEntity;

@Entity
@Table(name="Option")
public class Option extends CommonEntity{
	
	private static final long serialVersionUID = 1L;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Question question;
	
	@Basic
	private Integer number;
	@Basic
	private String title;
	@Basic
	private String description;
	@Basic
	private String weightage;
	
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getWeightage() {
		return weightage;
	}
	public void setWeightage(String weightage) {
		this.weightage = weightage;
	}
	@Override
	public String toString() {
		return "Option [number=" + number + ", title=" + title
				+ ", description=" + description + ", weightage=" + weightage
				+ "]";
	}

	
}
