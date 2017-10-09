package com.notes.nicefact.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import com.notes.nicefact.to.FileTO;

@Entity
public class TaskFile   extends AbstractFile{

	private static final long serialVersionUID = 1L;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Task task;
	
	
	public TaskFile() {
		super();
	}

	public TaskFile(FileTO file, String path) {
		super(file, path);
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

}
