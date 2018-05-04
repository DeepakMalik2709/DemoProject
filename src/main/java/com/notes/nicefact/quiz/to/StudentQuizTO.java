package com.notes.nicefact.quiz.to;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.quiz.entity.StudentQuiz;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StudentQuizTO{

	private Long id;
	
	private AppUser student;	
	
	private Integer correctQues;
	
	private Integer incorrectQues;
	
	private Integer notAttempted;
	
	private Integer score;
	
	private String status;
	
	private QuizTO quizTO;

	public StudentQuizTO(StudentQuiz studentQuiz) {
		this.id = studentQuiz.getId();
		this.student = studentQuiz.getStudent();
		this.correctQues = studentQuiz.getCorrectQues();
		this.incorrectQues = studentQuiz.getIncorrectQues();
		this.notAttempted =studentQuiz.getNotAttempted();
		this.score= studentQuiz.getScore();
		this.quizTO = new QuizTO(studentQuiz.getQuiz());
	}
	

	public StudentQuizTO() {
		// TODO Auto-generated constructor stub
	}

	
	
	public Integer getCorrectQues() {
		return correctQues;
	}

	public void setCorrectQues(Integer correctQues) {
		this.correctQues = correctQues;
	}

	public Integer getIncorrectQues() {
		return incorrectQues;
	}

	public void setIncorrectQues(Integer incorrectQues) {
		this.incorrectQues = incorrectQues;
	}

	public Integer getNotAttempted() {
		return notAttempted;
	}

	public void setNotAttempted(Integer notAttempted) {
		this.notAttempted = notAttempted;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public QuizTO getQuizTO() {
		return quizTO;
	}
	public void setQuizTO(QuizTO quizTO) {
		this.quizTO = quizTO;
	}
	public AppUser getStudent() {
		return student;
	}



	public void setStudent(AppUser student) {
		this.student = student;
	}



	public Long getId() {
		return id;
	}



	public void setId(Long id) {
		this.id = id;
	}
	
	
}
