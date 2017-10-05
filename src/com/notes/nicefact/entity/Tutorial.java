package com.notes.nicefact.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.notes.nicefact.enums.LANGUAGE;
import com.notes.nicefact.to.TagTO;
import com.notes.nicefact.to.TutorialTO;

@Entity
public class Tutorial extends CommonEntity {

	private static final long serialVersionUID = 1L;

	String title;

	@Column(columnDefinition = "TEXT")
	String description;

	String url;
	
	@Transient
	Set<Tag> tags;
	
	@ElementCollection(fetch = FetchType.EAGER)
	Set<Long> tagIds = new HashSet<>();
	
	@OneToMany(mappedBy = "tutorial", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	protected List<TutorialFile> files = new ArrayList<>();
	
	/*@Enumerated(EnumType.STRING)
	private LANGUAGE language = LANGUAGE.ENGLISH_AND_HINDI;
	*/

	@ElementCollection(targetClass = LANGUAGE.class, fetch = FetchType.EAGER)
	@Enumerated(EnumType.STRING)
	private Set<LANGUAGE> languages = new HashSet<>();
	
	public Tutorial(){}
	public Tutorial(TutorialTO tutorialTO) {
		this.title = tutorialTO.getTitle();
		this.description = tutorialTO.getDescription();
		this.url = tutorialTO.getUrl();
		if(null == tags){
			tags = new HashSet<>();
		}
		for (TagTO tagTO : tutorialTO.getTags()) {
			if(tagTO.getId() !=null && tagTO.getId() > 0){
				this.tagIds.add(tagTO.getId());
			}
		}
		if(null !=  tutorialTO.getLanguages()){
			this.languages = new HashSet<>( tutorialTO.getLanguages());
		}
	}
	
	public void updateProps(Tutorial tutorialTO) {
		this.title = tutorialTO.getTitle();
		this.description = tutorialTO.getDescription();
		this.url = tutorialTO.getUrl();
		this.tagIds = new HashSet<>(tutorialTO.getTagIds());
		this.languages = new HashSet<>();
		if(null !=  tutorialTO.getLanguages()){
			this.languages = new HashSet<>( tutorialTO.getLanguages());
		}
	}
	
	public Set<LANGUAGE> getLanguages() {
		if(null == languages){
			return new HashSet<>();
		}
		return languages;
	}

	public void setLanguages(Set<LANGUAGE> languages) {
		this.languages = languages;
	}


	public List<TutorialFile> getFiles() {
		if(null == files){
			files = new ArrayList<>();
		}
		return files;
	}
	public void setFiles(List<TutorialFile> files) {
		this.files = files;
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public Set<Tag> getTags() {
		if(null == tags){
			tags= new HashSet<>();
		}
		return tags;
	}
	public void setTags(Set<Tag> tags) {
		this.tags = tags;
	}
	
	public Set<Long> getTagIds() {
		if(null == tagIds){
			tagIds = new HashSet<>();
		}
		return tagIds;
	}
	public void setTagIds(Set<Long> tagIds) {
		this.tagIds = tagIds;
	}
}
