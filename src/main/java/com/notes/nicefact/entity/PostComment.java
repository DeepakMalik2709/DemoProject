package com.notes.nicefact.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PreUpdate;

import com.notes.nicefact.entity.AbstractRecipient.RecipientType;
import com.notes.nicefact.exception.AppException;
import com.notes.nicefact.to.CommentTO;
import com.notes.nicefact.to.PostRecipientTO;
import com.notes.nicefact.util.CacheUtils;

@Entity
public class PostComment extends AbstractComment {

	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch = FetchType.LAZY)
	private Post post;
	
	@ManyToOne(fetch = FetchType.LAZY)
    private PostComment parent;
   
	@OneToMany(mappedBy="parent", cascade=CascadeType.ALL)
    private List<PostComment> comments;


	@Basic
	int numberOfComments;
	
	int numberOfReactions = 0;
	
	@OneToMany(mappedBy="comment", cascade=CascadeType.ALL)
	protected List<CommentRecipient> recipients = new ArrayList<>();

	public PostComment() {
		super();
	}

	public PostComment(CommentTO commentTO) throws AppException {
		super(commentTO);
		CommentRecipient recipient;
		
		for (PostRecipientTO postRecipientTO : commentTO.getRecipients()) {
			recipient = new CommentRecipient();
			recipient.setEmail(postRecipientTO.getEmail());
			recipient.setName(postRecipientTO.getLabel());
			recipient.setComment(this);
			AppUser hr = CacheUtils.getAppUser(postRecipientTO.getEmail());
			if(hr == null){
				recipient.setType(RecipientType.EMAIL);
			}else{
				recipient.setType(RecipientType.USER);
				recipient.setName(hr.getDisplayName());
				recipient.setPosition(hr.getPosition());
				recipient.setDepartment(hr.getDepartment());
				recipient.setOrganization(hr.getOrganization());
			}
			this.recipients.add(recipient);
		}
	}
	
	public void updateProps(PostComment commentTO){
		super.updateProps(commentTO);
		for (CommentRecipient postRecipient : commentTO.getRecipients()) {
			postRecipient.setComment(this);
		}
		this.recipients = commentTO.getRecipients();
		this.isEdited=true;
	}


	public PostComment getParent() {
		return parent;
	}

	public void setParent(PostComment parent) {
		this.parent = parent;
	}

	public List<PostComment> getComments() {
		if(null == comments){
			comments = new ArrayList<>();
		}
		return comments;
	}

	public void setComments(List<PostComment> comments) {
		this.comments = comments;
	}

	public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}


	public int getNumberOfComments() {
		return numberOfComments;
	}

	public void setNumberOfComments(int numberOfComments) {
		this.numberOfComments = numberOfComments;
	}

	public int getNumberOfReactions() {
		return numberOfReactions;
	}

	public void setNumberOfReactions(int numberOfReactions) {
		this.numberOfReactions = numberOfReactions;
	}

	public List<CommentRecipient> getRecipients() {
		if(null == recipients){
			recipients = new ArrayList<>();
		}
		return recipients;
	}

	public void setRecipients(List<CommentRecipient> recipients) {
		this.recipients = recipients;
	}


	
	public List<String> getRecipientEmails() {
		List<String> emails = new ArrayList<>();
		for(CommentRecipient receipient : getRecipients()){
			emails.add(receipient.getEmail());
		}
		return emails;
	}

	
	@PreUpdate
	void prePersist(){
		super.preStore();
		this.numberOfComments = 0 ;
	}

}
