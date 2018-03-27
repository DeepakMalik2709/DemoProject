package com.notes.nicefact.quiz.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import com.notes.nicefact.entity.CommonEntity;

@Entity
public class QuizQuestion  extends CommonEntity{
	
	private static final long serialVersionUID = 1L;
	@ManyToOne(fetch = FetchType.LAZY)
	private Quiz quiz;
	@ManyToOne(fetch = FetchType.LAZY)
	private Question question;
	
	public QuizQuestion() {
		// TODO Auto-generated constructor stub
	}
	public QuizQuestion(Long quizQuestionId,Quiz quiz) {
		this.question = new Question();
		this.quiz= quiz;
		this.question.setId(quizQuestionId);
		
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
	
	
	
}
