package com.notes.nicefact.quiz.to;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.quiz.entity.AnsweredQuestion;
import com.notes.nicefact.quiz.enums.AnsweredQuesStatus;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AnsweredQuestionTO{

	private Long id;
	private QuizTO quizTO;
	private AppUser studentTO;
	private QuestionTO questionTO;
	private OptionTO optionTO;
	private AnsweredQuesStatus status;
	
	public AnsweredQuestionTO(AnsweredQuestion answeredQuestion) {
		this.id = answeredQuestion.getId();
		this.quizTO =new QuizTO(answeredQuestion.getQuiz());		
		this.optionTO=new OptionTO(answeredQuestion.getOption());
		this.questionTO=new QuestionTO(answeredQuestion.getQuestion());
		this.status = answeredQuestion.getStatus();
		this.studentTO = answeredQuestion.getStudent();
	}

	public AnsweredQuestionTO() {
	}
	
	public QuizTO getQuizTO() {
		return quizTO;
	}

	public void setQuizTO(QuizTO quizTO) {
		this.quizTO = quizTO;
	}

	public AppUser getStudentTO() {
		return studentTO;
	}

	public void setStudentTO(AppUser studentTO) {
		this.studentTO = studentTO;
	}

	public QuestionTO getQuestionTO() {
		return questionTO;
	}

	public void setQuestionTO(QuestionTO questionTO) {
		this.questionTO = questionTO;
	}

	public OptionTO getOptionTO() {
		return optionTO;
	}

	public void setOptionTO(OptionTO optionTO) {
		this.optionTO = optionTO;
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

	
}
