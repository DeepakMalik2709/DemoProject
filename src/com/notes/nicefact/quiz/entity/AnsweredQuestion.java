package com.notes.nicefact.quiz.entity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.CommonEntity;
import com.notes.nicefact.quiz.enums.AnsweredQuesStatus;
import com.notes.nicefact.quiz.to.AnsweredQuestionTO;

@Entity
public class AnsweredQuestion extends CommonEntity{
	
	private static final long serialVersionUID = 1L;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Quiz quiz;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private AppUser student;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Question question;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Option option;
	
	@Enumerated(EnumType.STRING)
	private AnsweredQuesStatus status = AnsweredQuesStatus.NOTCHECKED;

	public AnsweredQuestion(AnsweredQuestionTO answeredQuestionTO) {
		
	}

	public AnsweredQuestion() {
		// TODO Auto-generated constructor stub
	}

	public AppUser getStudent() {
		return student;
	}

	public Quiz getQuiz() {
		return quiz;
	}

	public void setQuiz(Quiz quiz) {
		this.quiz = quiz;
	}

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public AnsweredQuesStatus getStatus() {
		return status;
	}

	public void setStatus(AnsweredQuesStatus status) {
		this.status = status;
	}

	public void setStudent(AppUser student) {
		this.student = student;
	}

	public Option getOption() {
		return option;
	}

	public void setOption(Option option) {
		this.option = option;
	}
		
}
