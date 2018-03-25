package com.notes.nicefact.quiz.to;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StudentQuizTO{

	private QuestionTO  questionTO;
	private OptionTO selectOption;
	private String status;
	
	public QuestionTO getQuestionTO() {
		return questionTO;
	}
	public void setQuestionTO(QuestionTO questionTO) {
		this.questionTO = questionTO;
	}
	public OptionTO getSelectOption() {
		return selectOption;
	}
	public void setSelectOption(OptionTO selectOption) {
		this.selectOption = selectOption;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
