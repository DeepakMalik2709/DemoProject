package com.notes.nicefact.quiz.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import com.notes.nicefact.entity.CommonEntity;
import com.notes.nicefact.entity.Group;

@Entity
public class QuizGroupQuestion  extends CommonEntity{
	
	private static final long serialVersionUID = 1L;
	@ManyToOne(fetch = FetchType.LAZY)
	private Quiz quiz;
	@ManyToOne(fetch = FetchType.LAZY)
	private Group group;
	@ManyToOne(fetch = FetchType.LAZY)
	private Question question;
	
	public Quiz getQuiz() {
		return quiz;
	}
	public void setQuiz(Quiz quiz) {
		this.quiz = quiz;
	}
	public Group getGroup() {
		return group;
	}
	public void setGroup(Group group) {
		this.group = group;
	}
	public Question getQuestion() {
		return question;
	}
	public void setQuestion(Question question) {
		this.question = question;
	}
	
	
	
}
