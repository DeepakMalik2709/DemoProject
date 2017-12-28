package com.notes.nicefact.util;

public class Constants {

	public static final String MESSAGE = "message";
	public static final String CODE = "code";

	public static final int RESPONSE_OK = 0;
	public static final String TOTAL = "total";
	public static final int NO_RESULT = -1;
	public static final int ERROR_GENERAL = 1;
	public static final int ERROR_WITH_MSG = 2;
	public static final int RECORDS_40 = 40;
	public static final int RECORDS_20 = 20;
	public static final int RECORDS_10 = 10;
	public static final int RECORDS_5 = 5;
	public static final int RECORDS_100 = 100;


	public static final String DATA_MEMBER_GROUPS = "memberGroups";
	
	public static final String DATA_ITEMS = "items";
	
	public static final String LOGIN_USER = "loginUser";
	public static final String SESSION_INSTITUTES = "institutes";
	public static final String SESSION_INSTITUTE_MEMBERS = "instituteMembers";
	public static final String CONTEXT = "properties";

	public static final String NEXT_LINK = "nextLink";

	public static final String DATA_ITEM = "item";
	public static final String DATA_ACTIVITY = "activity";
	public static final String DATA_ACTIVITIES = "activities";
	public static final String PARAM_PAGE_NO = "pageNo";
	public static final String PARAM_OFFSET_STRING = "offset";
	public static final String PARAM_LIMIT_STRING = "limit";
	public static final String PARAM_SORT_ORDER_STRING = "sortOrder";
	public static final String PARAM_SORT_COLUMN_STRING = "sortColumn";
	public static final String PARAM_SEARCH_FIELDS = "searchFields";
	public static final String PARAM_JOIN_OPERATOR = "joinOperation";
	public static final String PARAM_RETURN_FIELDS = "returnFields";
	public static final String PARAM_SEARCH_TERM = "searchTerm";

	public static final String SESSION_KEY_lOGIN_USER = "loginUser";
	public static final String SESSION_KEY_USER_PREFERENCE = "userPreference";
	public static final String SESSION_KEY_COMMON_CONTEXT = "commonContext" ;
	
	public static final String UTF_8 = "UTF-8";

	public static final String PERSISTENSE_UNIT_NAME = "notes-pu";
	
	public static final String OPER_AND = "AND";
	public static final String OPER_OR = "OR";
	
	public static final String INDEX_PAGE = "/";
	
//	public static final String DASHBOARD_PAGE = "/a/secure/dashboard";
	public static final String DASHBOARD_PAGE = "/dashboard";
	public static final String HOME_PAGE = "/";
	public static final String PROFILE_PAGE = "/profile";
	
	public static final String OK = "OK";
	
	public static final long RESET_CODE_EXPIRES_TIME =  3*24*60*60*1000;
	public static final String DEFAULT_USER_IMAGE = "/img/users/user_1.jpg";
	public static final String PHOTO_URL = "/photo";
	public static final String PUBLIC_URL_PREPEND = "/rest/public/";
	public static final String SECURE_URL_PREPEND = "/rest/secure/";

	public static final String GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/auth";

	public static final String GOOGLE_OAUTH_TOKEN_URL = "https://accounts.google.com/o/oauth2/token";

	public static final String GOOGLE_SERVICE_ACCOUNT_SCOPES = "https://www.googleapis.com/auth/drive.file https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/datastore https://www.googleapis.com/auth/cloud-platform";

	public static final String GOOGLE_DRIVE_SCOPES = "https://www.googleapis.com/auth/drive";
	public static final String PROFILE_SCOPES = " https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile";
	public static final String GOOGLE_CALLBACK = "/googleCallback";
	
	public static final String NO_PREVIEW_AVAILABLE = "/img/no-preview-available.png";
	public static final String NO_PREVIEW_IMAGE = "/img/no-preview-image.png";
	public static final String NO_PREVIEW_PDF = "/img/no-preview-pdf.png";
	public static final String NO_PREVIEW_DOC = "/img/no-preview-doc.png";
	public static final String NO_PREVIEW_PPT = "/img/no-preview-ppt.png";
	public static final String NO_PREVIEW_EXCEL = "/img/no-preview-excel.png";
	
	public static final String DRIVE_FILE_UPLOAD_URL = "https://www.googleapis.com/upload/drive/v2/files";
	public static final String DRIVE_FILE_DOWNLOAD_URL = "https://www.googleapis.com/drive/v3/files/";
	
	public static final String DRVIE_FETCH_FIELDS = "parents,shared,alternateLink,createdDate,description,downloadUrl,editable,embedLink,exportLinks,fileSize,iconLink,id,labels,lastModifyingUser,lastModifyingUserName,lastViewedByMeDate,mimeType,modifiedByMeDate,modifiedDate,ownerNames,owners,selfLink,thumbnailLink,title,userPermission,webContentLink,writersCanShare";
	public static final String THUMBNAIL_FOLDER = "thumbnails/";
	public static final String CONTENT_TYPE_JSON = "application/json; charset=UTF-8";
	public static final String CONTENT_TYPE = "Content-Type";
	public static final String  LOCALHOST_ADDRESS = "http://127.0.0.1:8080";
	
	public static final String WRITER = "writer";
	public static final String COMMENTER = "commenter";
	public static final String OWNER = "owner";
	public static final String READER = "reader";
	public static final String ANYONE = "anyone";
	public static final String USER = "user";
	public static final String GROUP = "group";
	//public static final String REDIRECT_URL = "/a/public/login?redirect=";
	public static final String REDIRECT_URL = "/a/oauth/googleLogin?redirect=";
	public static final String DATE_FORMAT_1 = "dd-MMM-yy hh:mm a";
	public static final Long FIRST_LOGIN_TEST_GROUP = -905L;
	public static final String APPLICATION_URL = "url";
	
	
}
