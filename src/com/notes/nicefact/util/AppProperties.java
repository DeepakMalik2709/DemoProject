package com.notes.nicefact.util;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * 
 *
 */
public class AppProperties {
	private static final Logger logger = Logger.getLogger(AppProperties.class);
	private Properties props = new Properties();

	private static AppProperties appProperties;

	String filePath = "app.properties";

	private AppProperties() {
		try {
			/*
			 * if (SystemProperty.environment.value() == null) { filePath =
			 * "war/" + filePath ; // for running java main }
			 */
			props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath));
		} catch (IOException ioe) {
			throw new RuntimeException(ioe);
		}
	}

	public static AppProperties getInstance() {
		if (appProperties == null) {
			appProperties = new AppProperties();
		}
		return appProperties;
	}

	private static final String SQL_DEV_URL = "sql.url.dev";
	private static final String SQL_PROD_URL = "sql.url.prod";
	private static final String SQL_SANDBOX_URL = "sql.url.sandbox";
	private static final String IS_SANDBOX = "app.sandbox";
	
	private static final String googleClientId = "google.client.id";
	private static final String googleClientSecret = "google.client.secret";
	private static final String facebookClientId = "facebook.client.id";
	private static final String facebookClientSecret = "facebook.client.secret";
	private static final String twitterClientId = "twitter.client.id";
	private static final String twitterClientSecret = "twitter.client.secret";
	private static final String adminEmail = "admin.email";
	private static final String supportEmail = "support.email";
	private static final String applicationUrl = "applicationUrl";
	private static final String applicationUrlDev = "applicationUrl.dev";
	private static final String applicationUrlSandbox = "applicationUrl.sandbox";
	private static final String appName = "appName";
	private static final String emailSender = "email.sender";
	private static final String emailSenderPassword = "email.sender.password";
	private static final String userProfilePhoto = "folder.user.photo";
	private static final String groupUploads = "folder.group.uploads";
	private static final String tutorialUploads = "folder.tutorial.uploads";
	private static final String userProfilePhotoSandbox = "folder.user.photo.sandbox";
	private static final String groupUploadsSandbox = "folder.group.uploads.sandbox";
	private static final String tutorialUploadsSandbox = "folder.tutorial.uploads.sandbox";
	private static final String tutorialUploadsDev = "folder.tutorial.uploads.dev";
	private static final String userProfilePhotoDev = "folder.user.photo.dev";
	private static final String groupUploadsDev = "folder.group.uploads.dev";
	private static final String tempUploads = "folder.group.temp";
	private static final String tempUploadsDev = "folder.group.temp.dev";
	private static final String GOOGLE_SERVICE_ACCOUNT_ID = "google.service.account.id";
	private static final String GOOGLE_SERVICE_ACCOUNT_EMAIL = "google.service.account.email";
	private static final String GOOGLE_SERVICE_ACCOUNT_PASSWORD = "google.service.account.password";
	private static final String GOOGLE_SERVICE_ACCOUNT_PK_FILE_PATH = "google.service.account.pkfile";
	private static final String DRIVE_THUMBNAIL_FOLDER_ID = "drive.temp.thumbnail.folder.id";
	private static final String DRIVE_USER_UPLOAD_FOLDER_NAME = "drive.user.upload.folder.name";
	private static final String DRIVE_USER_UPLOAD_FOLDER_NAME_DEV = "drive.user.upload.folder.name.dev";
	private static final String DRIVE_USER_UPLOAD_FOLDER_NAME_SANDBOX = "drive.user.upload.folder.name.sandbox";

	public String getDriveUserUploadFolderName() {
		if (isSandbox()) {
			return props.getProperty(DRIVE_USER_UPLOAD_FOLDER_NAME_SANDBOX);
		} else if (isProduction()) {
			return props.getProperty(DRIVE_USER_UPLOAD_FOLDER_NAME);
		}
		return props.getProperty(DRIVE_USER_UPLOAD_FOLDER_NAME_DEV);
	}
	
	public String getGoogleClientId() {
		return props.getProperty(googleClientId);
	}

	public String getGoogleClientSecret() {
		return props.getProperty(googleClientSecret);
	}

	public String getAdminEmail() {
		return props.getProperty(adminEmail);
	}

	public String getSupportEmail() {
		return props.getProperty(supportEmail);
	}
	
	public String getApplicationUrl() {
		if (isSandbox()) {
			return props.getProperty(applicationUrlSandbox);
		} else if (isProduction()) {
			return props.getProperty(applicationUrl);
		} else {
			return props.getProperty(applicationUrlDev);
		}
	}

	public String getAppName() {
		return props.getProperty(appName);
	}

	public String getFacebookclientid() {
		return props.getProperty(facebookClientId);
	}

	public String getFacebookclientsecret() {
		return props.getProperty(facebookClientSecret);
	}

	public String getTwitterclientid() {
		return props.getProperty(twitterClientId);
	}

	public String getTwitterclientsecret() {
		return props.getProperty(twitterClientSecret);
	}

	public String getEmailSender() {
		return props.getProperty(emailSender);
	}

	public String getEmailSenderPassword() {
		return props.getProperty(emailSenderPassword);
	}

	public String getSqlUrl() {
		if (isSandbox()) {
			return props.getProperty(SQL_SANDBOX_URL);
		}else	if (isProduction()) {
			return props.getProperty(SQL_PROD_URL);
		} 
		return props.getProperty(SQL_DEV_URL);
	}

	public boolean isProduction() {
		// http://stackoverflow.com/questions/228477/how-do-i-programmatically-determine-operating-system-in-java
		String operSys = System.getProperty("os.name").toLowerCase();
		// logger.info("opersys : " + operSys);
		if (operSys.contains("nix") || operSys.contains("nux") || operSys.contains("aix")) {
			return true;
		}
		return false;
	}

	public boolean isSandbox() {
		logger.info("props.getProperty(IS_SANDBOX) : " + props.getProperty(IS_SANDBOX));
		logger.info(" Boolean.parseBoolean( props.getProperty(IS_SANDBOX) : " +  Boolean.parseBoolean( props.getProperty(IS_SANDBOX)));
		if(isProduction()){
			return Boolean.parseBoolean( props.getProperty(IS_SANDBOX));
		}
		return false;
	}
	
	public String getUserProfilePhotoFolder() {
		if (isSandbox()) {
			return props.getProperty(userProfilePhotoSandbox);
		}else if (isProduction()) {
			return props.getProperty(userProfilePhoto);
		} else {
			return props.getProperty(userProfilePhotoDev);
		}
	}

	public String getGroupUploadsFolder() {
		if (isSandbox()) {
			return props.getProperty(groupUploadsSandbox);
		}else if (isProduction()) {
			return props.getProperty(groupUploads);
		} else {
			return props.getProperty(groupUploadsDev);
		}
	}

	public String getTutorialUploadsFolder() {
		if (isSandbox()) {
			return props.getProperty(tutorialUploadsSandbox);
		}else if (isProduction()) {
			return props.getProperty(tutorialUploads);
		} else {
			return props.getProperty(tutorialUploadsDev);
		}
	}
	
	public String getTempUploadsFolder() {
		if (isProduction()) {
			return props.getProperty(tempUploads);
		} else {
			return props.getProperty(tempUploadsDev);
		}
	}

	public String getGoogleServiceAccountId() {
		return props.getProperty(GOOGLE_SERVICE_ACCOUNT_ID);
	}

	public String getGoogleServiceAccountEmail() {
		return props.getProperty(GOOGLE_SERVICE_ACCOUNT_EMAIL);
	}

	public String getGoogleServiceAccountPassword() {
		return props.getProperty(GOOGLE_SERVICE_ACCOUNT_PASSWORD);
	}

	public String getGoogleServiceAccountPKFilePath() {
		return props.getProperty(GOOGLE_SERVICE_ACCOUNT_PK_FILE_PATH);
	}
	
	public String getDriveThumbnailFolderId() {
		return props.getProperty(DRIVE_THUMBNAIL_FOLDER_ID);
	}
}
