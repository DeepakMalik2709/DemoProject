package com.notes.nicefact.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.notes.nicefact.to.AppUserTO;
import com.notes.nicefact.util.Constants;

@Entity
public class AppUser extends CommonEntity {
	private static final long serialVersionUID = -1727664102130129478L;
	
	public enum AUTHORIZED_SCOPES{
		DRIVE, CALENDAR, CONTACTS , HANGOUTS;
	}
	
	@Enumerated(EnumType.STRING)
	@ElementCollection(fetch = FetchType.EAGER)
	private Set<AUTHORIZED_SCOPES> scopes = new HashSet<>();

	private String refreshToken;
	
	private String refreshTokenAccountEmail;

	@Transient
	private String accessToken;

	private String email;

	private String firstName;

	private String lastName;

	private String phoneNumber;
	
	private String dob;
	private String homeTown;
	private String currentCity;
	private String about;
	
	@ElementCollection(fetch = FetchType.EAGER)
	private Set<String> firebaseChannelKeys ;
	
	@OneToMany(targetEntity=Certificate.class, mappedBy="appUser", fetch=FetchType.EAGER)
	private Set<Certificate> certificates;

	String password;

	String passwordResetCode;

	String verifyEmailCode;

	Boolean isVerified;

	Date passwordResetCodeGenDate;

	String previousPassword;

	Date passwordChagneDate;

	String position;

	String department;

	String organization;

	String photoUrl;

	String uploadedPhotoPath;
	
	
	/*send post mail if you member in group*/
	boolean sendGroupPostEmail = true;
	
	/*send mail if your tagged in group post*/
	boolean sendGroupPostMentionEmail = true;
	
	/*send mail if someone comments on your post*/
	boolean sendPostCommentedEmail = true;
	
	/*send mail if someone mentions you in a comment*/
	boolean sendCommentMentiondEmail = true;
	
	/*send mail if someone comments on a post you are tagged in*/
	boolean sendCommentOnMentiondPostEmail = true;
	
	/* send mail if someone replies to your comment */
	boolean sendCommentReplyEmail = true;
	
	/* send mail if someone comments or replies to a comment where you are not directly tagged or a sender */
	boolean sendCommentOnCommentEmail = true;
	
	boolean sendPostLikeEmail = false;
	
	boolean sendCommentLikeEmail = false;

	/*
	 * @Basic Set<String> roles;
	 */

	@Basic
	String address;

	@Basic
	String deleteComment;

	/* comments is for internal user , do not expose to UI */
	@Basic
	String comments;

	private String language;

	protected Boolean requestedDelete = false;

	String timezone;

	Long noOfTutorials;

	boolean isSuperAdmin = false;

	@ElementCollection(fetch = FetchType.EAGER)
	Set<Long> groupIds;
	
	@ElementCollection(fetch = FetchType.EAGER)
	Set<Long> instituteIds;
	
	@ElementCollection(fetch = FetchType.EAGER)
	Set<Long> joinRequestGroups;
	
	@ElementCollection(fetch = FetchType.EAGER)
	Set<Long> joinRequestInstitutes;

	@Basic
	@Enumerated(EnumType.STRING)
	GENDER gender;
	
	String googleDriveFolderId;
	
	String googleDriveAttachmentsFolderId;
	String googleDriveLibraryFolderId;
	
	String taskSubmissionFolderId;
	String tutorialFolderId;
	String scheduleFolderId;
	@Basic
	private Date googleDriveMsgDate;

	@Basic
	private Date addInstituteMsgDate; 

	
	@Basic
	private Long lastSeenNotificationId;
	
	public enum GENDER {
		MALE, FEMALE
	};

	public AppUser(AppUserTO appUser) {
		super();
		this.email = appUser.getEmail();
		this.firstName = appUser.getFirstName();
		this.lastName = appUser.getLastName();
		this.phoneNumber = appUser.getPhoneNumber();
		if (StringUtils.isNotBlank(appUser.getAddress())) {
			this.address = appUser.getAddress();
		}

	}

	public AppUser() {
		super();
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("id", id);
		map.put("email", email);
		map.put("firstName", firstName);
		map.put("lastName", lastName);
		map.put("phoneNumber", getPhoneNumber());
		map.put("dob", getDob());
		map.put("homeTown", getHomeTown());
		map.put("currentCity", getCurrentCity());
		map.put("about", getAbout());
		map.put("updatedTime", getUpdatedTime().getTime());
		map.put("createdTime", getCreatedTime().getTime());
		map.put("active", getIsActive());
		map.put("deleted", getIsDeleted());
		map.put("displayName", getDisplayName());
		map.put("gender", getGender());
		map.put("language", getLanguage());
		map.put("timezone", getTimezone());
		map.put("hasUploadedPhoto", StringUtils.isNotBlank(uploadedPhotoPath));
		map.put("noOfTutorials", getNoOfTutorials());
		map.put("groupIds", getGroupIds());
		map.put("isSuperAdmin", getIsSuperAdmin());
		map.put("photoUrl", Constants.PUBLIC_URL_PREPEND + this.email + Constants.PHOTO_URL);
		map.put("useGoogleDrive", getUseGoogleDrive());
		map.put("useGoogleCalendar", getUseGoogleCalendar());
		
		map.put("refreshTokenAccountEmail", getRefreshTokenAccountEmail());
		if(this.googleDriveMsgDate !=null){
			map.put("googleDriveMsgDate", this.googleDriveMsgDate.getTime());
		}
		if(this.addInstituteMsgDate !=null){
			map.put("addInstituteMsgDate", this.addInstituteMsgDate.getTime());
		}
		
		map.put("sendCommentMentiondEmail", getSendCommentMentiondEmail());
		map.put("sendCommentOnMentiondPostEmail", getSendCommentOnMentiondPostEmail());
		map.put("sendCommentReplyEmail", getSendCommentReplyEmail());
		map.put("sendCommentOnCommentEmail", getSendCommentOnCommentEmail());
		map.put("sendGroupPostEmail", getSendGroupPostEmail());
		map.put("sendGroupPostMentionEmail", getSendGroupPostMentionEmail());
		map.put("sendPostCommentedEmail", getSendPostCommentedEmail());
		
		return map;
	}

	public String getScheduleFolderId() {
		return scheduleFolderId;
	}

	public void setScheduleFolderId(String scheduleFolderId) {
		this.scheduleFolderId = scheduleFolderId;
	}
	public Set<AUTHORIZED_SCOPES> getScopes() {
		if(null == scopes){
			scopes = new HashSet<>();
		}
		return scopes;
	}

	public void setScopes(Set<AUTHORIZED_SCOPES> scopes) {
		this.scopes = scopes;
	}

	public Boolean getUseGoogleDrive() {
		return getScopes().contains(AUTHORIZED_SCOPES.DRIVE);
	}
	
	public Boolean getUseGoogleCalendar() {
		return getScopes().contains(AUTHORIZED_SCOPES.CALENDAR);
	}
	
	public String getTutorialFolderId() {
		return tutorialFolderId;
	}

	public void setTutorialFolderId(String tutorialFolderId) {
		this.tutorialFolderId = tutorialFolderId;
	}

	public Date getGoogleDriveMsgDate() {
		return googleDriveMsgDate;
	}

	public void setGoogleDriveMsgDate(Date googleDriveMsgDate) {
		this.googleDriveMsgDate = googleDriveMsgDate;
	}
	
	public boolean getSendCommentMentiondEmail() {
		return sendCommentMentiondEmail;
	}

	public void setSendCommentMentiondEmail(boolean sendCommentMentiondEmail) {
		this.sendCommentMentiondEmail = sendCommentMentiondEmail;
	}

	public boolean getSendCommentOnMentiondPostEmail() {
		return sendCommentOnMentiondPostEmail;
	}

	public void setSendCommentOnMentiondPostEmail(boolean sendCommentOnMentiondPostEmail) {
		this.sendCommentOnMentiondPostEmail = sendCommentOnMentiondPostEmail;
	}

	public boolean getSendCommentReplyEmail() {
		return sendCommentReplyEmail;
	}

	public void setSendCommentReplyEmail(boolean sendCommentReplyEmail) {
		this.sendCommentReplyEmail = sendCommentReplyEmail;
	}

	public boolean getSendCommentOnCommentEmail() {
		return sendCommentOnCommentEmail;
	}

	public void setSendCommentOnCommentEmail(boolean sendCommentOnCommentEmail) {
		this.sendCommentOnCommentEmail = sendCommentOnCommentEmail;
	}

	public void setSendGroupPostMentionEmail(boolean sendGroupPostMentionEmail) {
		this.sendGroupPostMentionEmail = sendGroupPostMentionEmail;
	}

	public void setSendPostLikeEmail(boolean sendPostLikeEmail) {
		this.sendPostLikeEmail = sendPostLikeEmail;
	}

	public boolean getSendGroupPostEmail() {
		return sendGroupPostEmail;
	}

	public void setSendGroupPostEmail(boolean sendGroupPostEmail) {
		this.sendGroupPostEmail = sendGroupPostEmail;
	}

	public boolean getSendGroupPostMentionEmail() {
		return sendGroupPostMentionEmail;
	}

	public void getSendGroupPostMentionEmail(boolean sendGroupPostMentionEmail) {
		this.sendGroupPostMentionEmail = sendGroupPostMentionEmail;
	}

	public boolean getSendPostLikeEmail() {
		return sendPostLikeEmail;
	}

	public void getSendPostLikeEmail(boolean sendPostLikeEmail) {
		this.sendPostLikeEmail = sendPostLikeEmail;
	}

	public boolean getSendCommentLikeEmail() {
		return sendCommentLikeEmail;
	}

	public void setSendCommentLikeEmail(boolean sendCommentLikeEmail) {
		this.sendCommentLikeEmail = sendCommentLikeEmail;
	}

	public String getGoogleDriveFolderId() {
		return googleDriveFolderId;
	}

	public void setGoogleDriveFolderId(String googleDriveFolderId) {
		this.googleDriveFolderId = googleDriveFolderId;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public Set<Long> getInstituteIds() {
		if (null == instituteIds) {
			this.instituteIds = new HashSet<>();
		}
		return instituteIds;
	}

	public void setInstituteIds(Set<Long> instituteIds) {
		this.instituteIds = instituteIds;
	}

	public Set<Long> getGroupIds() {
		if (null == groupIds) {
			this.groupIds = new HashSet<>();
		}
		return groupIds;
	}

	public void setGroupIds(Set<Long> groupIds) {
		this.groupIds = groupIds;
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

	public Long getNoOfTutorials() {
		if (null == noOfTutorials) {
			return 0l;
		}
		return noOfTutorials;
	}

	public boolean getSendPostCommentedEmail() {
		return sendPostCommentedEmail;
	}

	public void setSendPostCommentedEmail(boolean sendPostCommentedEmail) {
		this.sendPostCommentedEmail = sendPostCommentedEmail;
	}

	public boolean getIsSuperAdmin() {
		return isSuperAdmin;
	}

	public void setIsSuperAdmin(boolean isSuperAdmin) {
		this.isSuperAdmin = isSuperAdmin;
	}

	public void setNoOfTutorials(Long noOfTutorials) {
		this.noOfTutorials = noOfTutorials;
	}

	public Boolean getIsVerified() {
		return isVerified;
	}

	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
	}

	public GENDER getGender() {
		return gender;
	}

	public String getUploadedPhotoPath() {
		return uploadedPhotoPath;
	}

	public void setUploadedPhotoPath(String uploadedPhotoPath) {
		this.uploadedPhotoPath = uploadedPhotoPath;
	}

	public void setGender(GENDER gender) {
		this.gender = gender;
	}

	public String getVerifyEmailCode() {
		return verifyEmailCode;
	}

	public void setVerifyEmailCode(String verifyEmailCode) {
		this.verifyEmailCode = verifyEmailCode;
	}

	public Boolean getRequestedDelete() {
		return requestedDelete;
	}

	public void setRequestedDelete(Boolean requestedDelete) {
		this.requestedDelete = requestedDelete;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getPasswordResetCode() {
		return passwordResetCode;
	}

	public void setPasswordResetCode(String passwordResetCode) {
		this.passwordResetCode = passwordResetCode;
	}
	public Date getPasswordResetCodeGenDate() {
		return passwordResetCodeGenDate;
	}

	public void setPasswordResetCodeGenDate(Date passwordResetCodeGenDate) {
		this.passwordResetCodeGenDate = passwordResetCodeGenDate;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Date getPasswordChagneDate() {
		return passwordChagneDate;
	}

	public void setPasswordChagneDate(Date passwordChagneDate) {
		this.passwordChagneDate = passwordChagneDate;
	}

	public String getPreviousPassword() {
		return previousPassword;
	}

	public void setPreviousPassword(String previousPassword) {
		this.previousPassword = previousPassword;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = new BCryptPasswordEncoder().encode(password);
	}

	public String getPhotoUrl() {
		return photoUrl;
	}

	public void setPhotoUrl(String photoUrl) {
		this.photoUrl = photoUrl;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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


	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDeleteComment() {
		return deleteComment;
	}

	public void setDeleteComment(String deleteComment) {
		this.deleteComment = deleteComment;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getHomeTown() {
		return homeTown;
	}

	public void setHomeTown(String homeTown) {
		this.homeTown = homeTown;
	}

	public String getCurrentCity() {
		return currentCity;
	}

	public void setCurrentCity(String currentCity) {
		this.currentCity = currentCity;
	}

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	public Set<Certificate> getCertificates() {
		return certificates;
	}

	public void setCertificates(Set<Certificate> certificates) {
		this.certificates = certificates;
	}

	public String getRefreshTokenAccountEmail() {
		return refreshTokenAccountEmail;
	}

	public void setRefreshTokenAccountEmail(String refreshTokenAccountEmail) {
		this.refreshTokenAccountEmail = refreshTokenAccountEmail;
	}

	public String getDisplayName() {
		String displayName = "";
		if (StringUtils.isNotBlank(firstName)) {
			displayName = firstName;
		}

		if (StringUtils.isNotBlank(lastName)) {
			if (StringUtils.isBlank(displayName)) {
				displayName = lastName;
			} else {
				displayName += " " + lastName;
			}
		}

		return displayName;
	}

	

	public String getTaskSubmissionFolderId() {
		return taskSubmissionFolderId;
	}

	public void setTaskSubmissionFolderId(String taskSubmissionFolderId) {
		this.taskSubmissionFolderId = taskSubmissionFolderId;
	}
	public String getGoogleDriveAttachmentsFolderId() {
		return googleDriveAttachmentsFolderId;
	}

	public void setGoogleDriveAttachmentsFolderId(String googleDriveAttachmentsFolderId) {
		this.googleDriveAttachmentsFolderId = googleDriveAttachmentsFolderId;
	}

	public String getGoogleDriveLibraryFolderId() {
		return googleDriveLibraryFolderId;
	}

	public void setGoogleDriveLibraryFolderId(String googleDriveLibraryFolderId) {
		this.googleDriveLibraryFolderId = googleDriveLibraryFolderId;
	}

	public Date getAddInstituteMsgDate() {
		return addInstituteMsgDate;
	}

	public void setAddInstituteMsgDate(Date addInstituteMsgDate) {
		this.addInstituteMsgDate = addInstituteMsgDate;
	}

	public Set<Long> getJoinRequestGroups() {
		if (null == joinRequestGroups) {
			this.joinRequestGroups = new HashSet<>();
		}
		return joinRequestGroups;
	}

	public void setJoinRequestGroups(Set<Long> joinRequestGroups) {
		this.joinRequestGroups = joinRequestGroups;
	}

	public Set<Long> getJoinRequestInstitutes() {
		if (null == joinRequestInstitutes) {
			this.joinRequestInstitutes = new HashSet<>();
		}
		return joinRequestInstitutes;
	}

	public void setJoinRequestInstitutes(Set<Long> joinRequestInstitutes) {
		this.joinRequestInstitutes = joinRequestInstitutes;
	}

	public Long getLastSeenNotificationId() {
		return lastSeenNotificationId;
	}

	public void setLastSeenNotificationId(Long lastSeenNotificationId) {
		this.lastSeenNotificationId = lastSeenNotificationId;
	}

	public Set<String> getFirebaseChannelKeys() {
		if(null == firebaseChannelKeys){
			firebaseChannelKeys = new HashSet<>();
		}
		return firebaseChannelKeys;
	}

	public void setFirebaseChannelKeys(Set<String> firebaseChannelKeys) {
		this.firebaseChannelKeys = firebaseChannelKeys;
	}


	public void addFirebaseChannelKey(String firebaseChannelKey) {
		this.getFirebaseChannelKeys().add(firebaseChannelKey);
	}

	@Override
	public String toString() {
		return "AppUser [email=" + email + ", photoUrl=" + photoUrl + ", uploadedPhotoPath=" + uploadedPhotoPath + ", getDisplayName()=" + getDisplayName() + "]";
	}

	@PrePersist
	@PreUpdate
	public void preStore() {
		email = email.toLowerCase().trim();
		super.preStore();
	}
}