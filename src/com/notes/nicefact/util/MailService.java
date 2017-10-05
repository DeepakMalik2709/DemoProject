package com.notes.nicefact.util;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.notes.nicefact.entity.AbstractComment;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.Notification;
import com.notes.nicefact.entity.NotificationRecipient;
import com.notes.nicefact.entity.Post;
import com.notes.nicefact.enums.NotificationAction;




public class MailService {

	private static final Logger logger = Logger.getLogger(MailService.class.getName());
	
	public static MailService instance;
	
	public static MailService getInstance(){
		if(instance == null){
			instance = new MailService();
		}
		return instance;
	}
	
	private VelocityEngine velocityEngine;
	private AppProperties appProperties;
	
	private MailService() {
		Properties props = new Properties();
		props.put("resource.loader", "class");
		props.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
		velocityEngine = new VelocityEngine(props);
		velocityEngine.init();
		appProperties = AppProperties.getInstance();
	}
	
	public void sendPasswordResetInstructions(AppUser appUser) {
		Template t = velocityEngine.getTemplate(Utils.getTemplateFilePath("passwordReset"), Constants.UTF_8);
		VelocityContext context = getCommonVelocityContext(appUser);
		String passwordResetUrl = AppProperties.getInstance().getApplicationUrl() + "/a/public/resetPassword/" + appUser.getPasswordResetCode();
		context.put("passwordResetUrl", passwordResetUrl);
		logger.info(passwordResetUrl);
		StringWriter writer = new StringWriter();
		t.merge(context, writer);
	    String subject =  "Password reset for " + appProperties.getAppName();
		
		List<String> to = new ArrayList<String>();
		to.add(   appUser.getEmail());
		sendMessage(subject,writer , to);
	}
	

	
	
	
	
	public void sendPasswordChangeMail(AppUser appUser) {
		Template t = velocityEngine.getTemplate(Utils.getTemplateFilePath("passwordChange"), Constants.UTF_8);
		VelocityContext context = getCommonVelocityContext(appUser);
		StringWriter writer = new StringWriter();
		t.merge(context, writer);
	    String subject =  "Password changed for " + appProperties.getAppName() ;
		
		List<String> to = new ArrayList<String>();
		to.add(   appUser.getEmail());
		sendMessage(subject,writer , to);
	}
	
	public void sendVerifyEmailMail(AppUser appUser) {
		Template t = velocityEngine.getTemplate(Utils.getTemplateFilePath("emailVerify"), Constants.UTF_8);
		VelocityContext context = getCommonVelocityContext(appUser);
		String verifyEmailUrl =appProperties.getApplicationUrl() + "/a/public/verifyEmail/" + appUser.getVerifyEmailCode();
		context.put("verifyEmailUrl", verifyEmailUrl);
		logger.info(verifyEmailUrl);
		StringWriter writer = new StringWriter();
		t.merge(context, writer);
	    String subject =  appProperties.getAppName() +  " : Please verify your email address";
		
		List<String> to = new ArrayList<String>();
		to.add(   appUser.getEmail());
		sendMessage(subject,writer , to);
	}
	
	VelocityContext getCommonVelocityContext(){
		VelocityContext context = new VelocityContext();
		context.put("appName", appProperties.getAppName());
		context.put("appUrl", appProperties.getApplicationUrl());
		context.put("supportEmail", appProperties.getSupportEmail());
		context.put("loginUrl", appProperties.getApplicationUrl() + Constants.REDIRECT_URL);
		return context;
	}
	
	VelocityContext getCommonVelocityContext(AppUser appUser){
		VelocityContext context = getCommonVelocityContext();
		context.put("appUser", appUser);
		return context;
	}
	public void sendWelcomeMail(AppUser appUser) {
		Template t = velocityEngine.getTemplate(Utils.getTemplateFilePath("welcome"), Constants.UTF_8);
		VelocityContext context = getCommonVelocityContext(appUser);
		StringWriter writer = new StringWriter();
		t.merge(context, writer);
	    String subject =  "Welcome to " + appProperties.getAppName();
		
		List<String> to = new ArrayList<String>();
		to.add(   appUser.getEmail());
		sendMessage(subject,writer , to);
	}
	
	

	public void sendMessage(String subject, StringWriter htmlBody, List<String> toEmails) {
		Properties props = new Properties();

		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(appProperties.getEmailSender(), appProperties.getEmailSenderPassword());
			}
		});
		String fromEmail = appProperties.getEmailSender();
		try {
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(appProperties.getEmailSender(), appProperties.getAppName()));
			msg.setReplyTo(new Address[] { new InternetAddress(fromEmail) });
			logger.info("From: " + fromEmail + " To: " + toEmails);
			for (int i = 0; i < toEmails.size(); i++) {
				msg.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmails.get(i)));
			}

			Multipart mp = new MimeMultipart();
			MimeBodyPart htmlPart = new MimeBodyPart();
			htmlPart.setContent(htmlBody.toString(), "text/html;charset=utf-8");
			mp.addBodyPart(htmlPart);
			msg.setSubject(subject, Constants.UTF_8);
			msg.setContent(mp);

			for (int i = 0; i < 3; i++) {
				try {
					Transport.send(msg);
					break;
				} catch (MessagingException e) {
					Thread.sleep(2000);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Error sending email: ", e);
		}
	}

	public void sendGroupAddNotificationnEmail(  Notification notification, NotificationRecipient recipient) {
		String templateName = getTemplateName(recipient);
			Template t = velocityEngine.getTemplate(Utils.getTemplateFilePath(templateName), Constants.UTF_8);
			VelocityContext context = getCommonVelocityContext();
			context.put("notification", notification);
			StringWriter writer = new StringWriter();
			t.merge(context, writer);
			String subject = getNotificationSubject(recipient, notification);
			List<String> to = new ArrayList<String>();
			to.add(recipient.getEmail());
			sendMessage(subject, writer, to);
			logger.info("\n" + writer);
		
	}
	
	public void sendPostNotificationEmail(Post post , AbstractComment comment,  Notification notification, NotificationRecipient recipient) {
		String templateName = getTemplateName(recipient);
		if (null == templateName) {
			logger.error("template is null for action : " + recipient.getAction() + " , notifcation recipient id : " + recipient.getId());
		}else{
			Template t = velocityEngine.getTemplate(Utils.getTemplateFilePath(templateName), Constants.UTF_8);
			VelocityContext context = getCommonVelocityContext();
			context.put("notification", notification);
			context.put("recipient", recipient);
			context.put("post", post);
			context.put("comment", comment);
			StringWriter writer = new StringWriter();
			t.merge(context, writer);
			String subject = getNotificationSubject(recipient, notification);
			List<String> to = new ArrayList<String>();
			to.add(recipient.getEmail());
			sendMessage(subject, writer, to);
			logger.debug("\n" + writer);
		}
		
	}

	private String getTemplateName(NotificationRecipient recipient) {
		if(recipient.getAction() != null && StringUtils.isNotBlank(recipient.getAction().getMailTemplateName())){
			return recipient.getAction().getMailTemplateName();
		}
		return null;
	}

	private String getNotificationSubject(NotificationRecipient recipient , Notification notification) {
		String subject = "Notification from " + appProperties.getAppName();
		if(NotificationAction.COMMENTED_SENDER.equals(recipient.getAction())){
			subject = notification.getSenderName() +  " commented on your post.";
		}else if(NotificationAction.COMMENTED_MENTIONED_POST.equals(recipient.getAction())){
			subject = notification.getSenderName() +  " commented on a post that you are tagged in.";
		}else if(NotificationAction.REPLIED_SENDER.equals(recipient.getAction())){
			subject = notification.getSenderName() +  " replied to your comment.";
		}else if(NotificationAction.COMMENT_MENTIONED.equals(recipient.getAction())){
			subject = notification.getSenderName() +  " mentioned you in a comment.";
		}else if(NotificationAction.COMMENT_REPLY_MENTIONED.equals(recipient.getAction())){
			subject = notification.getSenderName() +  " mentioned you in a comment.";
		}else if(NotificationAction.REPLIED_MENTIONED_COMMENT.equals(recipient.getAction())){
			subject = notification.getSenderName() +  " replied to a comment that you are tagged in.";
		}else if(NotificationAction.COMMENT_FOLLOWING.equals(recipient.getAction())){
			subject = notification.getSenderName() +  " commented on a post your following.";
		}else if(NotificationAction.POST_MENTIONED.equals(recipient.getAction())){
			subject = notification.getSenderName() +  " tagged you in a post.";
		}else if(NotificationAction.POSTED_GROUP.equals(recipient.getAction())){
			subject = notification.getSenderName() +  " posted in " + notification.getGroupName();
		}else if(NotificationAction.GROUP_ADDED.equals(recipient.getAction())){
			subject = notification.getSenderName()  +  " added you to "  + notification.getGroupName();
		}else if(NotificationAction.POST_LIKE.equals(recipient.getAction())){
			subject = notification.getSenderName() +  " likes your post.";
		}else if(NotificationAction.POST_COMMENT_LIKE.equals(recipient.getAction())){
			subject = notification.getSenderName() +  "likes your comment.";
		}
		return subject;
	}

}
