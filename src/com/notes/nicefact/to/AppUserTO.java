package com.notes.nicefact.to;

import java.io.Serializable;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.AppUser.GENDER;
import com.notes.nicefact.util.Constants;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AppUserTO  implements Serializable  {
	
	private static final long serialVersionUID = 4603498092205954184L;

	private String email;


	private String firstName;

	private String lastName;
	
	private String language;
	
	private String displayName;

	private String phoneNumber;
	
	String address;

	Long id;
	
	long updatedTime;
	
	long createdTime;

	String photoUrl;
	
	String password;
	
	String timezone;
	
	Boolean active ;
	
	Boolean deleted;
	
	String position;
	
	String department;
	
	String organization;
	
	boolean hasUploadedPhoto ;
	
	
	/* check AppUser entity for comments */
	
	Boolean sendGroupPostEmail ;
	
	Boolean sendGroupPostMentionEmail ;
	
	Boolean sendPostCommentedEmail ;
	
	Boolean sendCommentMentiondEmail ;
	
	Boolean sendCommentOnMentiondPostEmail ;
	
	Boolean sendCommentReplyEmail ;
	
	Boolean sendCommentOnCommentEmail ;
	
	GENDER gender;
	long noOfTutorials;
	
	Set<Long> groupIds;
	
	public AppUserTO(AppUser appUser) {
		super();
		this.id = appUser.getId();
		this.email = appUser.getEmail();
		this.firstName = appUser.getFirstName();
		this.lastName = appUser.getLastName();
		this.phoneNumber = appUser.getPhoneNumber();
		this.updatedTime = appUser.getUpdatedTime().getTime();
		this.createdTime = appUser.getCreatedTime().getTime();
		this.active = appUser.getIsActive();
		this.deleted = appUser.getIsDeleted();
		this.displayName = appUser.getDisplayName();
		this.gender = appUser.getGender();
		this.language = appUser.getLanguage();
		this.timezone = appUser.getTimezone();
		//this.photoUrl = appUser.getPhotoUrl();
		this.hasUploadedPhoto = StringUtils.isNotBlank(appUser.getUploadedPhotoPath());
		this.photoUrl = Constants.PUBLIC_URL_PREPEND + appUser.getEmail() +  Constants.PHOTO_URL;
		this.noOfTutorials = appUser.getNoOfTutorials();
		this.groupIds = appUser.getGroupIds();
	}

	public AppUserTO() {
		super();
	}

	
	public boolean getHasUploadedPhoto() {
		return hasUploadedPhoto;
	}

	public void setHasUploadedPhoto(boolean hasUploadedPhoto) {
		this.hasUploadedPhoto = hasUploadedPhoto;
	}

	public long getNoOfTutorials() {
		return noOfTutorials;
	}

	public void setNoOfTutorials(long noOfTutorials) {
		this.noOfTutorials = noOfTutorials;
	}

	public Set<Long> getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(Set<Long> groupIds) {
		this.groupIds = groupIds;
	}

	public Boolean getActive() {
		if(null == active)
			return true;
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public Boolean getDeleted() {
		if(null == deleted){
			return false;
		}
		return deleted;
	}

	public void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public GENDER getGender() {
		return gender;
	}

	public void setGender(GENDER gender) {
		this.gender = gender;
	}

	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


	public String getPhotoUrl() {
		if(StringUtils.isBlank(photoUrl)){
			return "/img/user-pic.jpg";
		}
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getDisplayName() {
		if(StringUtils.isBlank(displayName)){
			return email;
		}
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}


	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getOrganization() {
		return organization;
	}

	public void setOrganization(String organization) {
		this.organization = organization;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}


	public String getUsername() {
		return email;
	}


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public long getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(long updatedTime) {
		this.updatedTime = updatedTime;
	}

	public long getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}
	
	public Boolean getSendCommentMentiondEmail() {
		return sendCommentMentiondEmail;
	}

	public void setSendCommentMentiondEmail(Boolean sendCommentMentiondEmail) {
		this.sendCommentMentiondEmail = sendCommentMentiondEmail;
	}

	public Boolean getSendCommentOnMentiondPostEmail() {
		return sendCommentOnMentiondPostEmail;
	}

	public void setSendCommentOnMentiondPostEmail(Boolean sendCommentOnMentiondPostEmail) {
		this.sendCommentOnMentiondPostEmail = sendCommentOnMentiondPostEmail;
	}

	public Boolean getSendCommentReplyEmail() {
		return sendCommentReplyEmail;
	}

	public void setSendCommentReplyEmail(Boolean sendCommentReplyEmail) {
		this.sendCommentReplyEmail = sendCommentReplyEmail;
	}

	public Boolean getSendCommentOnCommentEmail() {
		return sendCommentOnCommentEmail;
	}

	public void setSendCommentOnCommentEmail(Boolean sendCommentOnCommentEmail) {
		this.sendCommentOnCommentEmail = sendCommentOnCommentEmail;
	}

	public void setSendGroupPostMentionEmail(Boolean sendGroupPostMentionEmail) {
		this.sendGroupPostMentionEmail = sendGroupPostMentionEmail;
	}

	public Boolean getSendGroupPostEmail() {
		return sendGroupPostEmail;
	}

	public void setSendGroupPostEmail(Boolean sendGroupPostEmail) {
		this.sendGroupPostEmail = sendGroupPostEmail;
	}

	public Boolean getSendGroupPostMentionEmail() {
		return sendGroupPostMentionEmail;
	}

	public Boolean getSendPostCommentedEmail() {
		return sendPostCommentedEmail;
	}

	public void setSendPostCommentedEmail(Boolean sendPostCommentedEmail) {
		this.sendPostCommentedEmail = sendPostCommentedEmail;
	}

	@Override
	public String toString() {
		return "AppUserTO [email=" + email + ", displayName=" + displayName + "]";
	}
	


}
