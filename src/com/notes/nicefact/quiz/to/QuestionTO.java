package com.notes.nicefact.quiz.to;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.notes.nicefact.quiz.entity.Option;
import com.notes.nicefact.quiz.entity.Question;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionTO {
	private Long id;
	private String description;
	private String title;
	private Integer number;
	private String tag;
	private String type;
	private List<OptionTO> options;
	private Integer marks;

	public QuestionTO() {
	}

	public QuestionTO(Question question) {
		super();
		this.id=question.getId();
		this.setDescription(question.getDescription());
		this.title = question.getTitle();
		this.number = question.getNumber();
		this.tag = question.getTag();
		this.type = question.getType();
		this.marks = question.getMarks();
		options = new ArrayList<>();
		for(Option opts : question.getOptions()) {
			OptionTO optionTO = new OptionTO(opts);
			options.add(optionTO);
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public List<OptionTO> getOptions() {
		return options;
	}

	public void setOptions(List<OptionTO> options) {
		this.options = options;
	}

	public Integer getMarks() {
		return marks;
	}

	public void setMarks(Integer marks) {
		this.marks = marks;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
