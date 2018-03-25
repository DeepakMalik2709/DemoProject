package com.notes.nicefact.quiz.to;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.notes.nicefact.quiz.entity.Quiz;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QuizTO {
	Long id;
	private String name;
	private String subject;
	private String description;
	private Integer marks;
	private List<Long> groups;
	private List<Long> questions;
	private Long fromDateTime;
	private Long toDateTime;
	
	private String passingRules;
	private Integer totalAppeared;
	
	public QuizTO() {
	}
	
	public QuizTO(Quiz quiz) {
		// TODO Auto-generated constructor stub
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getMarks() {
		return marks;
	}

	public void setMarks(Integer marks) {
		this.marks = marks;
	}

	public List<Long> getGroups() {
		return groups;
	}

	public void setGroups(List<Long> groups) {
		this.groups = groups;
	}

	public List<Long> getQuestions() {
		return questions;
	}

	public void setQuestions(List<Long> questions) {
		this.questions = questions;
	}

	public Long getFromDateTime() {
		return fromDateTime;
	}

	public void setFromDateTime(Long fromDateTime) {
		this.fromDateTime = fromDateTime;
	}

	public Long getToDateTime() {
		return toDateTime;
	}

	public void setToDateTime(Long toDateTime) {
		this.toDateTime = toDateTime;
	}

	public String getPassingRules() {
		return passingRules;
	}

	public void setPassingRules(String passingRules) {
		this.passingRules = passingRules;
	}

	public Integer getTotalAppeared() {
		return totalAppeared;
	}

	public void setTotalAppeared(Integer totalAppeared) {
		this.totalAppeared = totalAppeared;
	}
	
	
	
}
