package com.notes.nicefact.enums;

public enum NotificationAction {

	/* someone posted in a group you follow */
	POSTED_GROUP(" posted in " ,  "notification_posted_group"), 
	/* someone mentioned you in a post */
	POST_MENTIONED(" tagged you in ",  "notification_posted_group"), 
	/* someone likes your post */
	POST_LIKE(" likes your post ", "notification_posted_group"),
	/* someone likes your comment */
	POST_COMMENT_LIKE(" likes your comment ", "notification_commented"), 
	/* someone commented on your post */
	COMMENTED_SENDER(" commented on your post ", "notification_commented"), 
	/* someone commented on a post your tagged in */
	COMMENTED_MENTIONED_POST(" commented on a post your tagged in ", "notification_commented"),
	/* someone mentioned you in a comment */
	COMMENT_MENTIONED(" mentioned you in a comment", "notification_commented"),
	/* someone replied to your comment */
	REPLIED_SENDER(" replied to your comment ", "notification_commented"), 
	/* someone replied to a comment that you were tagged in */
	REPLIED_MENTIONED_COMMENT(" replied to a comment your tagged in ", "notification_commented"),
	/* someone mentioned you in a reply to a comment */
	COMMENT_REPLY_MENTIONED(" mentioned you in a comment ", "notification_commented"),
	/* general case of notification */
	COMMENT_FOLLOWING( " commented on a post your following ", "notification_commented"),
	/* someone added you to a group or institute*/
	GROUP_ADDED(" added you to ", "notification_group_added"),
	INSTITUTE_ADDED(" added you to ", "notification_institute_added");

	String messageKey;
	
	String mailTemplateName ;

	NotificationAction(String message, String template) {
		messageKey = message;
		mailTemplateName = template;
	}

	public String getMessageKey() {
		return messageKey;
	}

	public String getMailTemplateName() {
		return mailTemplateName;
	}

}
