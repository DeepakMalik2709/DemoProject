package com.notes.nicefact.quiz.to;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.notes.nicefact.entity.Group;
import com.notes.nicefact.quiz.entity.Question;
import com.notes.nicefact.quiz.entity.Quiz;
import com.notes.nicefact.quiz.entity.TIME_STATUS;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QuizTO {
	Long id;
	private String name;
	private String subject;
	private String description;
	private Integer marks;
	private List<Long> groups;
	private List<QuestionTO> questions;
	private Long fromDateTime;
	private Long toDateTime;
	private boolean withTime;
	private String passingRules;
	private Integer totalAppeared;
	private String createdByEmail;
	private String shareWith;
	
	public QuizTO() {
	}
	
	public QuizTO(Quiz quiz) {
		this.id=quiz.getId();
		this.name = quiz.getName();
		this.subject = quiz.getSubject();
		this.description = quiz.getDescription();
		this.marks = quiz.getMarks();
		this.questions = new ArrayList<>();
		this.createdByEmail = quiz.getCreatedBy();
		this.groups = new ArrayList<>();
		this.fromDateTime = quiz.getFromDateTime();
		this.toDateTime = quiz.getToDateTime();
		this.passingRules = quiz.getPassingRules();
		this.totalAppeared = quiz.getTotalAppeared();		
		this.withTime = !quiz.getTimeStatus().equals(TIME_STATUS.NA);
		this.shareWith = quiz.getShareWith().toString();
		for (Question ques : quiz.getQuestions()) {			
			this.questions.add(new QuestionTO(ques));
		}
		for(Group quizGrp : quiz.getGroups()) {
			this.groups.add(quizGrp.getId());
		}
	
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

	public List<QuestionTO> getQuestions() {
		return questions;
	}

	public void setQuestions(List<QuestionTO> questions) {
		this.questions = questions;
	}

	public String getCreatedByEmail() {
		return createdByEmail;
	}

	public void setCreatedByEmail(String createdByEmail) {
		this.createdByEmail = createdByEmail;
	}

	public boolean getWithTime() {
		return withTime;
	}

	public void setWithTime(boolean withTime) {
		this.withTime = withTime;
	}

	public String getShareWith() {
		return shareWith;
	}

	public void setShareWith(String shareWith) {
		this.shareWith = shareWith;
	}

}
