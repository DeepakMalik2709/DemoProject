package com.notes.nicefact.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import com.notes.nicefact.to.FileTO;

@Entity
public class TutorialFile   extends AbstractFile{

	private static final long serialVersionUID = 1L;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Tutorial tutorial;
	
	
	public TutorialFile() {
		super();
	}

	public TutorialFile(FileTO file, String path) {
		super(file, path);
	}

	public Tutorial getTutorial() {
		return tutorial;
	}

	public void setTutorial(Tutorial tutorial) {
		this.tutorial = tutorial;
	}

}
