package com.notes.nicefact.quiz.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.CommonEntity;
import com.notes.nicefact.quiz.enums.AnsweredQuesStatus;

@Entity
public class AnsweredQuestion extends CommonEntity{
	
	private static final long serialVersionUID = 1L;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private AppUser student;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Question questionId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Option optinonId;
	
	@Enumerated(EnumType.STRING)
	private AnsweredQuesStatus status = AnsweredQuesStatus.NOTCHECKED;

	public AppUser getStudent() {
		return student;
	}

	public void setStudent(AppUser student) {
		this.student = student;
	}

	public Question getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Question questionId) {
		this.questionId = questionId;
	}

	public Option getOptinonId() {
		return optinonId;
	}

	public void setOptinonId(Option optinonId) {
		this.optinonId = optinonId;
	}

	public AnsweredQuesStatus getStatus() {
		return status;
	}

	public void setStatus(AnsweredQuesStatus status) {
		this.status = status;
	}
	
	
	
}
