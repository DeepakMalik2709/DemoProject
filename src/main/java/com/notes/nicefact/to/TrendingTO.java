package com.notes.nicefact.to;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.notes.nicefact.entity.Post;
import com.notes.nicefact.entity.PostTag;

@JsonIgnoreProperties(ignoreUnknown=true)
public class TrendingTO implements Serializable {

	private static final long serialVersionUID = 4269604958312653975L;

	private Long id;
	private String comment;
	private List<TagTO> tags = new ArrayList<>();
	private String createdBy;
	private Long createdTime;
	private String url;
	
	public TrendingTO(Post post) {
		this.id = post.getId();
		this.comment = post.getComment();
		this.createdBy = post.getCreatedByName();
		this.createdTime = post.getCreatedTime().getTime();
		
		if(post.getPostTags().size() > 0) {
			TagTO tagTO = null;
			
			for(PostTag postTag: post.getPostTags()) {
				tagTO = new TagTO();
				tagTO.setId(postTag.getTag().getId());
				tagTO.setName(postTag.getTag().getName());
				this.tags.add(tagTO);
			}
		}
		
		if(post.getFiles().size() > 0) {
			this.url = post.getFiles().get(0).getEmbedLink();
		}
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getComment() {
		return comment;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public List<TagTO> getTags() {
		return tags;
	}
	
	public void setTags(List<TagTO> tags) {
		this.tags = tags;
	}
	
	public String getCreatedBy() {
		return createdBy;
	}
	
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	
	public Long getCreatedTime() {
		return createdTime;
	}
	
	public void setCreatedTime(Long createdTime) {
		this.createdTime = createdTime;
	}
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}	
}