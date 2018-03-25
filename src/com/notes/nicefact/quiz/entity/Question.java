package com.notes.nicefact.quiz.entity;

import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="Question")
public class Question{
	@Basic
	private String descriton;
	@Basic
	private String title;
	@Basic
	private Integer number;
	@Basic
	private String tag;
	@Basic
	private String type;
	
	@OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Option> options;
	@Basic
	private Integer marks;
	
	public String getDescriton() {
		return descriton;
	}
	public void setDescriton(String descriton) {
		this.descriton = descriton;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Set<Option> getOptions() {
		return options;
	}
	public void setOptions(Set<Option> options) {
		this.options = options;
	}
	public Integer getMarks() {
		return marks;
	}
	public void setMarks(Integer marks) {
		this.marks = marks;
	}
	@Override
	public String toString() {
		return "Question [descriton=" + descriton + ", title=" + title
				+ ", number=" + number + ", tag=" + tag + ", type=" + type
				+ ", options=" + options + ", marks=" + marks + "]";
	}
	 
	 
	 
}
