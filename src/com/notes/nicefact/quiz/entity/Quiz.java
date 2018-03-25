package com.notes.nicefact.quiz.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.notes.nicefact.entity.CommonEntity;
import com.notes.nicefact.quiz.to.QuizTO;

@Entity
@Table(name="Quiz")
public class Quiz  extends CommonEntity{
	
	private static final long serialVersionUID = 1L;
	@Basic
	private String name;
	@Basic
	private String subject;
	@Basic
	private String description;
	@Basic
	private Integer marks;
	
	@OneToMany(mappedBy = "group", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<QuizGroupQuestion> groups=  new HashSet<>();
	
	@OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<QuizGroupQuestion> questions=  new HashSet<>();
	
	@Basic
	private Long fromDateTime;
	@Basic
	private Long toDateTime;
	@Basic
	private String passingRules;
	@Basic
	private Integer totalAppeared;
	
	public Quiz() {
		// TODO Auto-generated constructor stub
	}
	public Quiz(QuizTO quizTO) {
		// TODO Auto-generated constructor stub
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
	
	public Set<QuizGroupQuestion> getGroups() {
		return groups;
	}
	public void setGroups(Set<QuizGroupQuestion> groups) {
		this.groups = groups;
	}
	public Set<QuizGroupQuestion> getQuestions() {
		return questions;
	}
	public void setQuestions(Set<QuizGroupQuestion> questions) {
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
	@Override
	public String toString() {
		return "Quiz [name=" + name + ", subject=" + subject + ", description="
				+ description + ", marks=" + marks + ", groups=" + groups
				+ ", questions=" + questions + ", fromDateTime=" + fromDateTime
				+ ", toDateTime=" + toDateTime + ", passingRules="
				+ passingRules + ", totalAppeared=" + totalAppeared + "]";
	}
	
	public void updateProps(Quiz quiz) {
		
	}
}
