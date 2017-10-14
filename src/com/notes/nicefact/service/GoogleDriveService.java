package com.notes.nicefact.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.notes.nicefact.entity.AbstractFile;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.PostFile;
import com.notes.nicefact.to.FileTO;
import com.notes.nicefact.to.GoogleDriveFile;
import com.notes.nicefact.to.GoogleFilePermission;
import com.notes.nicefact.util.Constants;
import com.notes.nicefact.util.Utils;

/**
 * service for google drive
 * 
 * @author Jitender
 * 
 */
public class GoogleDriveService {
	public static final Logger logger = Logger.getLogger(GoogleDriveService.class.getName());
	/**
	 * End point url for google drive
	 */

	public static final String DRIVE_API_BASE_URL = "https://www.googleapis.com/drive/";

	public static final String SPREADSHEETS_BASE_URL = "https://sheets.googleapis.com/";

	public static final String DRIVE_API_VERSION2 = "v2/";

	public static final String DRIVE_API_VERSION3 = "v3/";

	public static final String DRIVE_API_VERSION4 = "v4/";

	public static final String DRIVE_API_URL = DRIVE_API_BASE_URL + DRIVE_API_VERSION2;

	public static final String SPREADSHEETS_API_URL = SPREADSHEETS_BASE_URL + "v4/spreadsheets/";

	public static final String DRIVE_FILES_URL = DRIVE_API_URL + "files/";

	private static final String DRIVE_FILE_UPLOAD_URL = "https://www.googleapis.com/upload/drive/v2/files";

	public static final int USER_CANNOT_SHARE = -1;

	public static final int USER_CAN_SHARE = 0;

	public static final int USER_CAN_SHARE_AND_CHANGE_SHARING = 1;

	public static final String DEFAULT_PROPERTY_VISIBILITY = "PUBLIC";

	/**
	 * enum to represent different files mime types
	 * 
	 */
	public enum GoogleFileTypes {
		DOCUMENT("application/vnd.google-apps.document", ".doc"), PRESENTATION("application/vnd.google-apps.presentation", ".ppt"), SPREADSHEET("application/vnd.google-apps.spreadsheet",
				".xls"), DRAWING("application/vnd.google-apps.drawing", ".png"), FOLDER("application/vnd.google-apps.folder", ""), FORM("application/vnd.google-apps.form", "");

		String mimeType;
		String extension;

		GoogleFileTypes(String mime, String ext) {
			mimeType = mime;
			extension = ext;
		}

		public String getMimeType() {
			return mimeType;
		}

		public String getExtension() {
			return extension;
		}

		public GoogleFileTypes getFromMimeType(String mime) {
			for (GoogleFileTypes type : this.values()) {
				if (type.mimeType.equals(mime)) {
					return type;
				}
			}
			return null;
		}

	};

	private GoogleDriveService() {
	}

	public static GoogleDriveService instance;

	public static GoogleDriveService getInstance() {
		if (instance == null) {
			instance = new GoogleDriveService();
		}
		return instance;
	}

	public HttpResponse doGet(String url, List<Header> headers, AppUser user) {
		logger.info("doGet start , url : " + url);
		HttpClient client = HttpClients.createDefault();
		if (StringUtils.isBlank(user.getAccessToken())) {
			Utils.refreshToken(user);
		}
		for (int i = 0; i < 3; i++) {
			try {
				HttpGet request1 = new HttpGet(url);
				logger.info("attempt : " + i);
				request1.addHeader("Authorization", "Bearer " + user.getAccessToken());
				if (headers != null) {
					for (Header header : headers) {
						request1.addHeader(header);
					}
				}
				HttpResponse response = client.execute(request1);
				if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 201 || response.getStatusLine().getStatusCode() == 204) {
					return response;
				} else if (response.getStatusLine().getStatusCode() == 401) {
					Utils.refreshToken(user);
				} else if (response.getEntity() != null) {
					String respStr = new String(IOUtils.toByteArray(response.getEntity().getContent()), Constants.UTF_8);
					logger.info(respStr);
				}
				request1.releaseConnection();
				Thread.sleep((i * 1000) + new Random().nextInt(1000));
			} catch (Exception e) {
				if (i == 2) {
					logger.error(user.getEmail() + " , failed request , " + e.getMessage(), e);
				}
			}
		}
		logger.info("doGet exit");
		return null;
	}

	public HttpResponse doDelete(String url, List<Header> headers, AppUser user) {
		logger.info("doDelete start , url : " + url);
		HttpClient client = HttpClients.createDefault();
		if (StringUtils.isBlank(user.getAccessToken())) {
			Utils.refreshToken(user);
		}
		for (int i = 0; i < 3; i++) {
			try {
				HttpDelete request1 = new HttpDelete(url);
				logger.info("attempt : " + i);
				request1.addHeader("Authorization", "Bearer " + user.getAccessToken());
				if (headers != null) {
					for (Header header : headers) {
						request1.addHeader(header);
					}
				}
				HttpResponse response = client.execute(request1);
				if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 201 || response.getStatusLine().getStatusCode() == 204) {
					return response;
				} else if (response.getStatusLine().getStatusCode() == 401) {
					Utils.refreshToken(user);
				} else if (response.getEntity() != null) {
					String respStr = new String(IOUtils.toByteArray(response.getEntity().getContent()), Constants.UTF_8);
					logger.info(respStr);
				}
				request1.releaseConnection();
				Thread.sleep((i * 1000) + new Random().nextInt(1000));
			} catch (Exception e) {
				if (i == 2) {
					logger.error(user.getEmail() + " , failed request , " + e.getMessage(), e);
				}
			}
		}
		logger.info("doDelete exit");
		return null;
	}

	public JSONObject doJsonGet(String url, List<Header> headers, AppUser user) {
		HttpResponse resp = doGet(url, headers, user);
		if (null == resp) {
			logger.error(user.getEmail() + ", " + url + " , null response");
		}else{
			return getJsonFromResponse(resp);
		}
		return null;
	}

	public JSONObject doJsonDelete(String url, List<Header> headers, AppUser user) {
		HttpResponse resp = doDelete(url, headers, user);
		if (null == resp) {
			logger.error(user.getEmail() + ", " + url + " , null response");
		} else {
			return getJsonFromResponse(resp);
		}
		return null;
	}

	public HttpResponse doPost(String url, List<Header> headers, byte[] payload, AppUser user) {
		logger.info("doPost start , url : " + url);
		HttpClient client = HttpClients.createDefault();
		if (StringUtils.isBlank(user.getAccessToken())) {
			Utils.refreshToken(user);
		}
		for (int i = 0; i < 3; i++) {
			try {
				HttpPost request1 = new HttpPost(url);
				logger.info("attempt : " + i);
				request1.addHeader("Authorization", "Bearer " + user.getAccessToken());
				if (headers != null) {
					for (Header header : headers) {
						request1.addHeader(header);
					}
				}
				if (null != payload) {
					ByteArrayEntity entity = new ByteArrayEntity(payload);
					request1.setEntity(entity);
				}
				HttpResponse response = client.execute(request1);
				if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 201 || response.getStatusLine().getStatusCode() == 204) {
					return response;
				} else if (response.getStatusLine().getStatusCode() == 401) {
					Utils.refreshToken(user);
				} else if (response.getEntity() != null) {
					String respStr = new String(IOUtils.toByteArray(response.getEntity().getContent()), Constants.UTF_8);
					logger.info(respStr);
				}
				request1.releaseConnection();
				Thread.sleep((i * 1000) + new Random().nextInt(1000));
			} catch (Exception e) {
				if (i == 2) {
					logger.error(user.getEmail() + " , failed request , " + e.getMessage(), e);
				}
			}
		}
		logger.info("doPost exit");
		return null;
	}

	public JSONObject doJsonPost(String url, List<Header> headers, byte[] payload, AppUser user) {
		HttpResponse resp = doPost(url, headers, payload, user);
		if (null == resp) {
			logger.error(user.getEmail() + ", " + url + " , null response");
		} else {
			return getJsonFromResponse(resp);
		}
		return null;
	}

	public HttpResponse doPut(String url, List<Header> headers, byte[] payload, AppUser user) {
		logger.info("doPut start , url : " + url );
		HttpClient client = HttpClients.createDefault();
		logger.info("url : " + url);
		for (int i = 0; i < 3; i++) {
			try {
				HttpPut request1 = new HttpPut(url);
				logger.info("attempt : " + i);
				request1.addHeader("Authorization", "Bearer " +  user.getAccessToken());
				if (headers != null) {
					for (Header header : headers) {
						request1.addHeader(header);
					}
				}
				if (null != payload) {
					ByteArrayEntity entity = new ByteArrayEntity(payload);
					request1.setEntity(entity);
				}
				HttpResponse response = client.execute(request1);
				if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 201 || response.getStatusLine().getStatusCode() == 204) {
					return response;
				} else if (response.getStatusLine().getStatusCode() == 401) {
					Utils.refreshToken(user);
				} else if (response.getEntity() != null) {
					String respStr = new String(IOUtils.toByteArray(response.getEntity().getContent()), Constants.UTF_8);
					logger.info(respStr);
				}
				request1.releaseConnection();
			} catch (Exception e) {
				if (i == 2) {
					logger.error(user.getEmail() + " , failed request , " + e.getMessage(), e);
				}
			}
		}
		logger.info("doPut exit");
		return null;
	}

	public JSONObject doJsonPut(String url, List<Header> headers, byte[] payload, AppUser user) {
		HttpResponse resp = doPut(url, headers, payload, user);
		if (null == resp) {
			logger.error(user.getEmail() + ", " + url + " , null response");
		} else {
			return getJsonFromResponse(resp);
		}
		return null;
	}

	public HttpResponse doPatch(String url, List<Header> headers, byte[] payload, AppUser user) {
		logger.info("doPatch start , url : " + url);
		HttpClient client = HttpClients.createDefault();
		logger.info("url : " + url);
		for (int i = 0; i < 3; i++) {
			try {
				HttpPatch request1 = new HttpPatch(url);
				logger.info("attempt : " + i);
				request1.addHeader("Authorization", "Bearer " + user.getAccessToken());
				if (headers != null) {
					for (Header header : headers) {
						request1.addHeader(header);
					}
				}
				if (null != payload) {
					ByteArrayEntity entity = new ByteArrayEntity(payload);
					request1.setEntity(entity);
				}
				HttpResponse response = client.execute(request1);
				if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 201 || response.getStatusLine().getStatusCode() == 204) {
					return response;
				} else if (response.getStatusLine().getStatusCode() == 401) {
					Utils.refreshToken(user);
				} else if (response.getEntity() != null) {
					String respStr = new String(IOUtils.toByteArray(response.getEntity().getContent()), Constants.UTF_8);
					logger.info(respStr);
				}
				request1.releaseConnection();
			} catch (Exception e) {
				if (i == 2) {
					logger.error(user.getEmail() + " , failed request , " + e.getMessage(), e);
				}
			}
		}
		logger.info("doPatch exit");
		return null;
	}

	public JSONObject doJsonPatch(String url, List<Header> headers, byte[] payload, AppUser user) {
		HttpResponse resp = doPatch(url, headers, payload, user);
		if (null == resp) {
			logger.error(user.getEmail() + ", " + url + " , null response");
		} else {
			return getJsonFromResponse(resp);
		}
		return null;
	}

	public GoogleDriveFile createNewFile(String title, GoogleFileTypes fileType, AppUser user) {
		GoogleDriveFile file = null;
		try {
			JSONObject metadata = new JSONObject();
			metadata.put("title", title);
			metadata.put("convert", "true");
			metadata.put("mimeType", fileType.getMimeType());
			List<Header> headers = new ArrayList<>();
			headers.add(new BasicHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_JSON));
			String url = DRIVE_FILES_URL;
			JSONObject resp = doJsonPost(url, headers, metadata.toString().getBytes(Constants.UTF_8), user);
			if (null == resp) {
				logger.error("cannot make file , : " + fileType + " , name : " + title);
			} else {
				file = new GoogleDriveFile(resp);
			}
		} catch (JSONException | UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
		}
		return file;
	}

	public GoogleFilePermission updatePermission(String fileId, String permissionId, String role, String type, String value, Boolean needLinkToAccess, boolean sendNotificationEmails, AppUser user) {
		logger.info("updatePermission start");
		JSONObject postData = new JSONObject();
		try {
			role = role.toLowerCase();
			if (role.equals(Constants.COMMENTER)) {
				JSONArray additionalRoles = new JSONArray();
				additionalRoles.put(Constants.COMMENTER);
				postData.put("role", Constants.READER);
				postData.put("additionalRoles", additionalRoles);
			} else {
				postData.put("role", role);
			}
			postData.put("withLink", needLinkToAccess);
			if (StringUtils.isNotEmpty(type)) {
				postData.put("type", type.toLowerCase());
			}
			if (StringUtils.isNotEmpty(value)) {
				postData.put("value", value.toLowerCase());
			}
		} catch (JSONException e) {
			logger.error(e.getMessage(), e);
		}

		GoogleFilePermission updatedPermission = updatePermission(fileId, permissionId, postData, user, sendNotificationEmails, needLinkToAccess);
		if (permissionId == null && updatedPermission != null && StringUtils.isNotBlank(updatedPermission.getRole()) && !role.equals(updatedPermission.getRole())
				&& !Constants.OWNER.equalsIgnoreCase(updatedPermission.getRole())) {
			updatedPermission = updatePermission(fileId, updatedPermission.getId(), postData, user, sendNotificationEmails, needLinkToAccess);
		}
		logger.info("updatePermission exit");
		return updatedPermission;
	}

	public GoogleFilePermission updatePermission(String fileId, String permissionId, JSONObject postData, AppUser user, boolean sendNotificationEmails, Boolean withLink) {

		GoogleFilePermission googleFilerPermission = null;

		String postDataStr = postData.toString();
		logger.info("postDataStr: " + postDataStr);
		String url = DRIVE_FILES_URL + fileId + "/permissions";
		if (StringUtils.isNotBlank(permissionId)) {
			url += "/" + permissionId;
		}
		url += "?sendNotificationEmails=" + sendNotificationEmails + "&withLink=" + withLink;
		List<Header> headers = new ArrayList<>();
		headers.add(new BasicHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_JSON));
		try {
			byte[] payload = postDataStr.getBytes(Constants.UTF_8);
			JSONObject resp = null;
			if (StringUtils.isBlank(permissionId)) {
				resp = doJsonPost(url, headers, payload, user);
			} else {
				resp = doJsonPut(url, headers, payload, user);
			}

			if (resp != null) {
				googleFilerPermission = new GoogleFilePermission(resp);
			}
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
		}

		return googleFilerPermission;
	}

	public List<GoogleFilePermission> getFilePermissions(String fileId, AppUser hr) {
		List<GoogleFilePermission> permissions = new ArrayList<GoogleFilePermission>();
		String url = DRIVE_FILES_URL + fileId + "/permissions";

		JSONObject permissionJson = doJsonGet(url, null, hr);
		try {
			if (permissionJson != null) {
				JSONArray permissionArray = permissionJson.getJSONArray("items");
				for (int i = 0; i < permissionArray.length(); i++) {
					JSONObject permission = permissionArray.getJSONObject(i);
					permission.put("fileId", fileId);
					GoogleFilePermission filePermission = new GoogleFilePermission(permission);
					permissions.add(filePermission);
				}
			}

		} catch (JSONException e) {
			logger.error(e.getMessage(), e);
		}
		return permissions;
	}

	public GoogleDriveFile getFileFields(String fileId, String fields, AppUser user) {
		fields = addRequiredFields(fields);
		String url = null;
		try {
			url = DRIVE_FILES_URL + fileId + "?fields=" + URLEncoder.encode(fields, "utf-8");
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
		}
		GoogleDriveFile driveFile = null;
		JSONObject responseJson = doJsonGet(url, null, user);
		if (responseJson != null) {
			driveFile = new GoogleDriveFile(responseJson);
		}
		return driveFile;
	}

	public GoogleDriveFile getFileFieldsServiceAccount(String fileId, String fields) {
		fields = addRequiredFields(fields);
		String url = null;
		try {
			url = DRIVE_FILES_URL + fileId + "?fields=" + URLEncoder.encode(fields, "utf-8");
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
		}
		GoogleDriveFile driveFile = null;
		HttpResponse response = makeServiceAccountGetRequest(url, null);
		JSONObject responseJson = getJsonFromResponse(response);
		if (responseJson != null) {
			driveFile = new GoogleDriveFile(responseJson);
		}
		return driveFile;
	}

	public void moveFile(String fileId, String parentId, AppUser user) {
		logger.info("moveFile start");
		String url = DRIVE_FILES_URL + fileId + "/parents";
		JSONObject postData = new JSONObject();
		try {
			postData.put("id", parentId);
			String postDataStr = postData.toString();
			List<Header> headers = new ArrayList<>();
			headers.add(new BasicHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_JSON));
			HttpResponse response = doPost(url, headers, postDataStr.getBytes(Constants.UTF_8), user);
		} catch (JSONException | UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
		}
		logger.info("moveFile exit");
	}
	
	public void moveFileServiceAccount(String fileId, String parentId) {
		String url = DRIVE_FILES_URL + fileId + "/parents";
		JSONObject postData = new JSONObject();
		try {
			postData.put("id", parentId);

			String postDataStr = postData.toString();

			List<Header> headers = new ArrayList<>();
			headers.add(new BasicHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_JSON));
			HttpResponse response = makeServiceAccountPostRequest(url, headers, postDataStr.getBytes(Constants.UTF_8));
		} catch (JSONException | UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
		}

	}

	private String addRequiredFields(String query) {
		String updatedQuery = StringUtils.isBlank(query) ? "" : query;
		String[] requiredFields = { "id", "title", "alternateLink", "mimeType" };
		for (String field : requiredFields) {
			if (!updatedQuery.contains(field)) {
				if (StringUtils.isBlank(updatedQuery)) {
					updatedQuery = field;
				} else {
					updatedQuery = updatedQuery + "," + field;
				}

			}
		}
		return updatedQuery;
	}

	private JSONObject getJsonFromResponse(HttpResponse response) {
		JSONObject json = null;
		if (null != response && response.getEntity() != null) {
			try {
				String respStr = new String(IOUtils.toByteArray(response.getEntity().getContent()), Constants.UTF_8);
				json = new JSONObject(respStr);
			} catch (JSONException | UnsupportedOperationException | IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
		return json;
	}

	public HttpResponse makeServiceAccountGetRequest(String url, List<Header> headers) {
		HttpResponse response = null;
		logger.info("makeServiceAccountRequest start, url : " + url);
		String token = Utils.getGoolgeServiceAccountToken();
		HttpClient client = HttpClients.createDefault();
		for (int i = 0; i < 3; i++) {
			try {
				HttpGet request1 = new HttpGet(url);
				logger.info("attempt : " + i);
				request1.addHeader("Authorization", "Bearer " + token);
				if (headers != null) {
					for (Header header : headers) {
						request1.addHeader(header);
					}
				}

				response = client.execute(request1);
				if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 201 || response.getStatusLine().getStatusCode() == 204) {
					return response;
				} else if (response.getStatusLine().getStatusCode() == 401) {
					token = Utils.refreshServiceAccoungToken();
				} else if (response.getEntity() != null) {
					String respStr = new String(IOUtils.toByteArray(response.getEntity().getContent()), Constants.UTF_8);
					logger.error(respStr);
				}
				request1.releaseConnection();
				Thread.sleep((i * 1000) + new Random().nextInt(1000));
			} catch (Exception e) {
				if (i == 2) {
					logger.error("failed request service account, " + e.getMessage(), e);
				}
			}
		}
		logger.info("makeServiceAccountRequest exit");
		return null;
	}

	public HttpResponse makeServiceAccountPostRequest(String url, List<Header> headers, byte[] payload) {
		HttpResponse response = null;
		logger.info("makeServiceAccountRequest start, url : " + url);
		String token = Utils.getGoolgeServiceAccountToken();
		HttpClient client = HttpClients.createDefault();
		for (int i = 0; i < 3; i++) {
			try {
				HttpPost request1 = new HttpPost(url);
				logger.info("attempt : " + i);
				request1.addHeader("Authorization", "Bearer " + token);
				if (headers != null) {
					for (Header header : headers) {
						request1.addHeader(header);
					}
				}
				if (null != payload) {
					// request1.addHeader("Content-Length", payload.length +
					// "");
					ByteArrayEntity entity = new ByteArrayEntity(payload);
					request1.setEntity(entity);
				}

				response = client.execute(request1);
				if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 201 || response.getStatusLine().getStatusCode() == 204) {
					return response;
				} else if (response.getStatusLine().getStatusCode() == 401) {
					token = Utils.refreshServiceAccoungToken();
				} else if (response.getEntity() != null) {
					String respStr = new String(IOUtils.toByteArray(response.getEntity().getContent()), Constants.UTF_8);
					logger.error(respStr);
				}
				request1.releaseConnection();
				Thread.sleep((i * 1000) + new Random().nextInt(1000));
			} catch (Exception e) {
				if (i == 2) {
					logger.error("failed request service account, " + e.getMessage(), e);
				}
			}
		}
		logger.info("makeServiceAccountRequest exit");
		return null;
	}

	public GoogleDriveFile uploadFileToUserAccount(AbstractFile file, AppUser user) {
		logger.info("uploadFileToUserAccount start, postFile id  : " + file.getId());
		GoogleDriveFile driveFile = null;
		byte[] fileBytes = Utils.readFileBytes(file.getPath());
		if (null == fileBytes) {
			logger.error("File not found");
		} else {
			String url = Constants.DRIVE_FILE_UPLOAD_URL + "?uploadType=media";

			List<Header> headers = new ArrayList<>();
			headers.add(new BasicHeader(Constants.CONTENT_TYPE, file.getMimeType()));
			JSONObject response = doJsonPost(url, headers, fileBytes, user);

			if (null != response) {
				logger.info(response);
				driveFile = new GoogleDriveFile(response);
			}
		}
		logger.info("uploadFileToUserAccount exit");
		return driveFile;
	}

	public GoogleDriveFile uploadFileToServiceAccount(AbstractFile file) {
		logger.info("uploadFileServiceAccount start, postFile id  : " + file.getId());
		GoogleDriveFile driveFile = null;
		byte[] fileBytes = Utils.readFileBytes(file.getPath());
		if (null == fileBytes) {
			logger.error("File not found");
		} else {
			String url = Constants.DRIVE_FILE_UPLOAD_URL + "?uploadType=media";

			List<Header> headers = new ArrayList<>();
			headers.add(new BasicHeader(Constants.CONTENT_TYPE, file.getMimeType()));
			HttpResponse response = makeServiceAccountPostRequest(url, headers, fileBytes);

			if (null != response) {
				try {
					JSONObject respStr = getJsonFromResponse(response);
					logger.info(respStr);
					driveFile = new GoogleDriveFile(respStr);
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		logger.info("uploadFileServiceAccount exit");
		return driveFile;
	}

	public void deleteFileServiceAccount(String fileId) {
		logger.info("deleteFileServiceAccount start, postFile id  : " + fileId);
		String url = DRIVE_FILES_URL + fileId;
		makeServiceAccountDeleteRequest(url);
		logger.info("deleteFileServiceAccount exit");

	}

	public HttpResponse makeServiceAccountDeleteRequest(String url) {
		HttpResponse response = null;
		logger.info("makeServiceAccountDeleteRequest start, url : " + url);
		String token = Utils.getGoolgeServiceAccountToken();
		HttpClient client = HttpClients.createDefault();
		for (int i = 0; i < 3; i++) {
			try {
				HttpDelete request1 = new HttpDelete(url);
				logger.info("attempt : " + i);
				request1.addHeader("Authorization", "Bearer " + token);

				response = client.execute(request1);
				if (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 201 || response.getStatusLine().getStatusCode() == 204) {
					logger.info("file deleted at " + url);
					return response;
				} else if (response.getStatusLine().getStatusCode() == 401) {
					token = Utils.refreshServiceAccoungToken();
				} else if (response.getEntity() != null) {
					String respStr = new String(IOUtils.toByteArray(response.getEntity().getContent()), Constants.UTF_8);
					logger.error(respStr);
				}
				request1.releaseConnection();
				Thread.sleep((i * 1000) + new Random().nextInt(1000));
			} catch (Exception e) {
				if (i == 2) {
					logger.error("failed request service account, " + e.getMessage(), e);
				}
			}
		}
		logger.info("makeServiceAccountDeleteRequest exit");
		return null;
	}

	public GoogleDriveFile renameFile(String fileId, String name, AppUser user) {
		JSONObject metadata = new JSONObject();
		try {
			metadata.put("title", name);
		} catch (JSONException e) {
			logger.error(e.getMessage(), e);
		}
		return updateFileMetadata(fileId, user, metadata);
	}

	public GoogleDriveFile updateFileMetadata(String fileId, AppUser user, JSONObject metadata) {
		logger.info("updatefile metadata start");
		GoogleDriveFile file = null;
		String url = DRIVE_FILES_URL + fileId;
		String meta = metadata.toString();
		List<Header> headers = new ArrayList<>();
		headers.add(new BasicHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_JSON));
		JSONObject fileJson;
		try {
			fileJson = doJsonPatch(url, headers, meta.getBytes(Constants.UTF_8), user);
			if (fileJson != null) {
				file = new GoogleDriveFile(fileJson);
			}
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
		}
		logger.info("updatefile metadata exit");
		
		return file;
	}
	
	public byte[] downloadFile(AbstractFile file, AppUser user) {
		logger.info("download File  start");
		byte[] fileBytes = null;
		String url = Constants.DRIVE_FILE_DOWNLOAD_URL + file.getGoogleDriveId() + "?alt=media";
		try {
			List<Header> headers = new ArrayList<>();
			headers.add(new BasicHeader(Constants.CONTENT_TYPE, file.getMimeType()));

			HttpResponse response = doGet(url, headers, user);
			if (response.getEntity() != null) {
				InputStream inStream = response.getEntity().getContent();
				fileBytes = IOUtils.toByteArray(inStream);
			}
		} catch (UnsupportedOperationException e) {
			logger.error(e.getMessage(), e);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		logger.info("downloadFile  end");
		return fileBytes;

	}

	public GoogleDriveFile uploadInputStreamFile(PostFile postFile,	AppUser user, byte[] bytes) {
		logger.info("upload Input Stream File  start");
		GoogleDriveFile driveFile= null;
		JSONObject response=null;
		FileTO fileTO =new FileTO(postFile.getName(), postFile.getServerName(), postFile.getMimeType(), postFile.getSizeBytes());		
		PostFile newfile  = new PostFile(fileTO,postFile.getPath());		
		String url = Constants.DRIVE_FILE_UPLOAD_URL + "?uploadType=media";
		List<Header> headers = new ArrayList<>();
		headers.add(new BasicHeader(Constants.CONTENT_TYPE, newfile.getMimeType()));
		
		response = doJsonPost(url, headers, bytes , user);
		if (null != response) {
			logger.info(response);
			driveFile = new GoogleDriveFile(response);
		}
		logger.info("upload Input Stream File  end");
		return driveFile;
	}
}
