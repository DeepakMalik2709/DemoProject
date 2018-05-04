package com.notes.nicefact.quiz.to;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.notes.nicefact.quiz.entity.Option;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OptionTO{
	private Long id;
	private Integer number;
	private String title;
	private String description;
	private String weightage;
	
	public OptionTO() {
		// TODO Auto-generated constructor stub
	}
	
	public OptionTO(Option option) {
		super();
		this.id = option.getId();
		this.number=option.getNumber();
		this.title=option.getTitle();
		this.description=option.getDescription();
		this.weightage=option.getWeightage();
		
	}
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
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	
}
