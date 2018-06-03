package com.notes.nicefact.quiz.entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.notes.nicefact.entity.CommonEntity;

@Entity
@Table(name="Question")
public class Question extends CommonEntity{
	private static final long serialVersionUID = 1L;
	@Basic
	private String description;
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
	
	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "questions")
	private Set<Quiz> quizes=  new HashSet<>();
	
	public Question() {
		// TODO Auto-generated constructor stub
	}
	
	public Question(Question question) {
		// TODO Auto-generated constructor stub
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
		return "Question [descriton=" + description + ", title=" + title
				+ ", number=" + number + ", tag=" + tag + ", type=" + type
				+ ", options=" + options + ", marks=" + marks + "]";
	}

	public Set<Quiz> getQuizes() {
		return quizes;
	}

	public void setQuizes(Set<Quiz> quizes) {
		this.quizes = quizes;
	}
	
	/*@Basic
	private List<String> imageURL;
	

	public List<String> getImageURL() {
		return imageURL;
	}

	public void setImageURL(List<String> imageURL) {
		this.imageURL = imageURL;
	}*/
	 
	 
	 
}
