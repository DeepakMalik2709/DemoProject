package com.notes.nicefact.to;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.notes.nicefact.entity.Tag;
import com.notes.nicefact.entity.Tutorial;
import com.notes.nicefact.entity.TutorialFile;
import com.notes.nicefact.enums.LANGUAGE;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TutorialTO {

	String title;
	
	String description;
	
	String url;
	
	String createdBy;
	
	long id;
	
	long createdTime ;
	
	long lastModifiedTime;

	public TutorialTO(){}
	
	LANGUAGE language ;
	
	List<TagTO> tags = new ArrayList<>();
	
	List<FileTO> files = new ArrayList<>();
	List<LANGUAGE> languages = new ArrayList<>();
	public TutorialTO(Tutorial tutorial) {
		this.title = tutorial.getTitle();
		this.description = tutorial.getDescription();
		this.url = tutorial.getUrl();
		this.createdBy = tutorial.getCreatedBy();
		this.id = tutorial.getId();
		this.createdTime = tutorial.getCreatedTime().getTime();
		this.lastModifiedTime = tutorial.getUpdatedTime().getTime();
		this.languages.addAll(tutorial.getLanguages());
		TagTO tagTO ;
		for (Tag tag : tutorial.getTags()) {
			tagTO = new TagTO(tag);
			this.tags.add(tagTO);
		}
		
		FileTO fileTO;
		for(TutorialFile file : tutorial.getFiles()){
			fileTO= new FileTO(file);
			this.files.add(fileTO);
		}
	}

	public List<LANGUAGE> getLanguages() {
		return languages;
	}

	public void setLanguages(List<LANGUAGE> languages) {
		this.languages = languages;
	}
	public List<FileTO> getFiles() {
		return files;
	}

	public void setFiles(List<FileTO> files) {
		this.files = files;
	}
	
	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public LANGUAGE getLanguage() {
		return language;
	}

	public void setLanguage(LANGUAGE language) {
		this.language = language;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public long getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}

	public long getLastModifiedTime() {
		return lastModifiedTime;
	}

	public void setLastModifiedTime(long lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<TagTO> getTags() {
		return tags;
	}

	public void setTags(List<TagTO> tags) {
		this.tags = tags;
	}
}
