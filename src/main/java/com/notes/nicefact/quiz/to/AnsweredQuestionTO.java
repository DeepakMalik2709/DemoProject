package com.notes.nicefact.quiz.to;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.quiz.entity.AnsweredQuestion;
import com.notes.nicefact.quiz.enums.AnsweredQuesStatus;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AnsweredQuestionTO{

	private Long id;
	private Long quizId;
	private String studentId;
	private Long questionId;
	private Long optionId;
	private AnsweredQuesStatus status;
	
	public AnsweredQuestionTO(AnsweredQuestion answeredQuestion) {
		this.id = answeredQuestion.getId();
		this.quizId =answeredQuestion.getQuiz().getId();		
		this.optionId=answeredQuestion.getOption().getId();
		this.questionId=answeredQuestion.getQuestion().getId();
		this.status = answeredQuestion.getStatus();
		this.studentId = answeredQuestion.getStudent().getEmail();
	}

	public AnsweredQuestionTO() {
	}
	
	

	public AnsweredQuestionTO(long quizId) {
		this.quizId =quizId;	
	}

	public AnsweredQuesStatus getStatus() {
		return status;
	}

	public void setStatus(AnsweredQuesStatus status) {
		this.status = status;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getQuizId() {
		return quizId;
	}

	public void setQuizId(Long quizId) {
		this.quizId = quizId;
	}

	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}

	public Long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(Long questionId) {
		this.questionId = questionId;
	}

	public Long getOptionId() {
		return optionId;
	}

	public void setOptionId(Long optionId) {
		this.optionId = optionId;
	}

	
}
