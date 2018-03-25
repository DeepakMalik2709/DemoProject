package com.notes.nicefact.quiz.to;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.notes.nicefact.quiz.entity.Option;
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionTO{
	 private String descriton;
	 private String title;
	 private int number;
	 private String tag;
	 private String type;
	 private List<Option> options;
	 private int marks;
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
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
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
	public List<Option> getOptions() {
		return options;
	}
	public void setOptions(List<Option> options) {
		this.options = options;
	}
	public int getMarks() {
		return marks;
	}
	public void setMarks(int marks) {
		this.marks = marks;
	}
	 
	 
	 
}
