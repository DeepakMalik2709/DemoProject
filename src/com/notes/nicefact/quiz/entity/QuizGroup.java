package com.notes.nicefact.quiz.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import com.notes.nicefact.entity.CommonEntity;
import com.notes.nicefact.entity.Group;

@Entity
public class QuizGroup  extends CommonEntity{
	
	private static final long serialVersionUID = 1L;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Quiz quiz;
	@ManyToOne(fetch = FetchType.LAZY)
	private Group group;
	
	public QuizGroup() {
		// TODO Auto-generated constructor stub
	}
	
	public QuizGroup(Long quizGroupId) {
		super();
		this.group = new Group();
		this.group.setId(quizGroupId);		
	}
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

	@PreUpdate
	@PrePersist
	void prePersist() {
		super.preStore();
	}
}
