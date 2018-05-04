package com.notes.nicefact.quiz.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import com.notes.nicefact.entity.CommonEntity;
import com.notes.nicefact.entity.Group;
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
	
	@Enumerated(EnumType.STRING)
	private TIME_STATUS timeStatus;
	
	@Enumerated(EnumType.STRING)
	private SHARE_TYPE shareWith;
	
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "quiz_group", 
	joinColumns = { @JoinColumn(name = "quiz_id", nullable = false, updatable = false) }, 
	inverseJoinColumns = { @JoinColumn(name = "group_id",nullable = false, updatable = false) })
	private Set<Group> groups=  new HashSet<>();
	
	@ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinTable(name = "quiz_question", 
	joinColumns = {@JoinColumn(name = "quiz_id", nullable = false, updatable = false) }, 
	inverseJoinColumns = { @JoinColumn(name = "question_id",nullable = false, updatable = false) })
	private Set<Question> questions=  new HashSet<>();
	
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
		super();
		updateProps( quizTO);
		
		
	}
	public TIME_STATUS getTimeStatus() {
		return timeStatus;
	}
	public void setTimeStatus(TIME_STATUS timeStatus) {
		this.timeStatus = timeStatus;
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
	
	public Set<Group> getGroups() {
		return groups;
	}
	public void setGroups(Set<Group> groups) {
		this.groups = groups;
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
				+ ", questions="  + ", fromDateTime=" + fromDateTime
				+ ", toDateTime=" + toDateTime + ", passingRules="
				+ passingRules + ", totalAppeared=" + totalAppeared + "]";
	}
	
	public void updateProps(QuizTO quizTO) {
		this.name=quizTO.getName();
		this.subject=quizTO.getSubject();
		this.description=quizTO.getDescription();
		this.marks=quizTO.getMarks();
		this.fromDateTime=quizTO.getFromDateTime();
		this.toDateTime=quizTO.getToDateTime();
		this.passingRules=quizTO.getPassingRules();
		this.totalAppeared=quizTO.getTotalAppeared();
		this.timeStatus=quizTO.getWithTime()?TIME_STATUS.BOTH:TIME_STATUS.NA;
		this.shareWith =SHARE_TYPE.valueOf(quizTO.getShareWith());
	}
	
	@PrePersist
	@PreUpdate
	void prePersist() {
		super.preStore();
	
		
	}
	public Set<Question> getQuestions() {
		return questions;
	}
	public void setQuestions(Set<Question> questions) {
		this.questions = questions;
	}
	public SHARE_TYPE getShareWith() {
		return shareWith;
	}
	public void setShareWith(SHARE_TYPE shareWith) {
		this.shareWith = shareWith;
	}
}
