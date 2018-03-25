package com.notes.nicefact.quiz.entity;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.CommonEntity;

@Entity
public class StudentQuiz  extends CommonEntity{
	
	private static final long serialVersionUID = 1L;
	@ManyToOne(fetch = FetchType.LAZY)
	private Quiz  quiz;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private AppUser student;
	
	@Basic
	private Integer correctQues;
	@Basic
	private Integer incorrectQues;
	@Basic
	private Integer notAttempted;
	@Basic
	private Integer score;
	
	@Enumerated(EnumType.STRING)
	private String status;
	
	public Quiz getQuiz() {
		return quiz;
	}
	public void setQuiz(Quiz quiz) {
		this.quiz = quiz;
	}
	public AppUser getStudent() {
		return student;
	}
	public void setStudent(AppUser student) {
		this.student = student;
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
	@Override
	public String toString() {
		return "StudentQuiz [quiz=" + quiz + ", student=" + student
				+ ", correctQues=" + correctQues + ", incorrectQues="
				+ incorrectQues + ", notAttempted=" + notAttempted + ", score="
				+ score + ", status=" + status + "]";
	}
	
	
	
}
