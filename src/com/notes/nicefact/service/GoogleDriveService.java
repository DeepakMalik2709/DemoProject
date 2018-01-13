package com.notes.nicefact.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.persistence.EntityManager;

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

import com.notes.nicefact.dao.GroupMemberDAO;
import com.notes.nicefact.dao.PostDAO;
import com.notes.nicefact.entity.AbstractFile;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.Group;
import com.notes.nicefact.entity.GroupMember;
import com.notes.nicefact.entity.Post;
import com.notes.nicefact.entity.PostFile;
import com.notes.nicefact.exception.ServiceException;
import com.notes.nicefact.to.GoogleDriveFile;
import com.notes.nicefact.to.GoogleFilePermission;
import com.notes.nicefact.to.MoveFileTO;
import com.notes.nicefact.util.CacheUtils;
import com.notes.nicefact.util.Constants;
import com.notes.nicefact.util.EntityManagerHelper;
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
				} else if (response.getStatusLine().getStatusCode() == 404) {
					return null;
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
					if(respStr.contains("invalidSharingRequest")){
						return null;
					}
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
		logger.info("createNewFile start");
		GoogleDriveFile file = null;
		try {
			JSONObject metadata = new JSONObject();
			metadata.put("title", title);
			metadata.put("convert", "true");
			metadata.put("mimeType", fileType.getMimeType());
			logger.info(metadata + " , for : " + user.getEmail()); 
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
		logger.info("exit createNewFile");
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
			}else if(!sendNotificationEmails){
				return updatePermission(fileId, permissionId, postData, user, true, withLink);
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

	void moveFile(String fileId, String parentId, AppUser user) {
		logger.info("moveFile start , fileId : " + fileId +" , parentId:  " + parentId );
		String url = DRIVE_FILES_URL + fileId + "/parents";
		JSONObject postData = new JSONObject();
		try {
			postData.put("id", parentId);
			String postDataStr = postData.toString();
			logger.info(postDataStr);
			List<Header> headers = new ArrayList<>();
			headers.add(new BasicHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_JSON));
			HttpResponse response = doPost(url, headers, postDataStr.getBytes(Constants.UTF_8), user);
			if(null != response){
				removeRootParent(fileId, user);
			}
		} catch (JSONException | UnsupportedEncodingException e) {
			logger.error(e.getMessage(), e);
		}
		logger.info("moveFile exit");
	}
	
	private void removeRootParent(String fileId, AppUser user) {
		logger.info("removeRootParent start");
		String url = DRIVE_FILES_URL + fileId + "/parents";
		try {
			JSONObject response = doJsonGet(url, null, user);
			if(response.has("items")){
				JSONArray array = response.getJSONArray("items");
				for(int i =0 ; i<array.length() ;i++){
					JSONObject item = array.getJSONObject(i);
					if(item.optBoolean("isRoot")){
						url = url + "/" + item.getString("id");
						doDelete(url, null, user);
						break;
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		logger.info("removeRootParent exit");
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
			if (response!=null && response.getEntity() != null) {
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
		String url = Constants.DRIVE_FILE_UPLOAD_URL + "?uploadType=media";
		List<Header> headers = new ArrayList<>();
		headers.add(new BasicHeader(Constants.CONTENT_TYPE, postFile.getMimeType()));
		
		response = doJsonPost(url, headers, bytes , user);
		if (null != response) {
			logger.info(response);
			driveFile = new GoogleDriveFile(response);
			renameFile(driveFile.getId(), postFile.getName(), user);
		}
		logger.info("upload Input Stream File  end");
		return driveFile;
	}
	
	public enum FOLDER{
		AllSchool, Attachments, Library, Task_Submission, Assignment, Post, Task, Schedule, Tutorial
	}
	
	
	/**
	 * 
	 * usage example
	 * MoveFileTO moveFileTO =  MoveFileTO.getInstances().setFileId(driveFile.getId()).setFileOwner(user.getEmail()).setGroupId(task.getGroupId()).addParents( FOLDER.Attachments, FOLDER.Task).setUser(user);
		moveFile(moveFileTO);
	 * @param fileId 
	 * @param groupId
	 * @param user
	 * @param parents
	 */
	public void moveFile(MoveFileTO moveFileTO) {
		logger.info("start moveFile , moveFileTO : "+  moveFileTO);
		if(moveFileTO.getFileIds().isEmpty() &&  moveFileTO.isTest()){
			logger.warn("file ids is empty : " + moveFileTO);
			return ;
		}
		String fileParentId =null;
		fileParentId = verifyUserFolders(FOLDER.AllSchool, moveFileTO.getUser());

		if (StringUtils.isNotBlank(fileParentId)) {
				for(FOLDER parent : moveFileTO.getParents()){
					fileParentId = verifyUserFolders(parent,  moveFileTO.getUser());
				}
		}
		
		
		if( moveFileTO.getGroupId() !=null ){
			FOLDER parent = moveFileTO.getParents().get(0);
			switch (parent) {
			case Attachments:
				fileParentId =  verifyPublicGroupFolderId(moveFileTO);
				break;
			case Library:
				fileParentId =  verifyLibraryGroupFolderId(moveFileTO);
				break;
			case Task_Submission:	
				fileParentId =  verifyTaskSubmissionGroupFolderId(moveFileTO);
				break;
			default:
				throw new ServiceException("first parent error in moveFileTO.getParents() : " + parent);
			}
		}
		
		if(StringUtils.isBlank(fileParentId)){
			logger.warn("fileParentId is null so exit without moving ");
		}else if( moveFileTO.getGroupId() !=null ){
			Group group = CacheUtils.getGroup(moveFileTO.getGroupId());
			List<FOLDER> parentsList = moveFileTO.getParents();
			for(FOLDER parent : parentsList){
				if(parentsList.contains(FOLDER.Attachments)){
					switch (parent) {
					case Post:
						fileParentId = group.getPostFolderId();
						break;
					case Task:
						fileParentId = group.getTaskFolderId();
						break;
					case Schedule:
						fileParentId = group.getScheduleFolderId();
						break;
						/*
					case Assignment:
						fileParentId = group.getAssignmentFolderId();
						break;*/
					
					}
				}
			}
		}
		
		if(StringUtils.isNotBlank(fileParentId)){
			for(String id : moveFileTO.getFileIds()){
				moveFile(id, fileParentId, moveFileTO.getUser());
			}
		}
		logger.info("exit moveFile");
	}
	
	private String verifyTaskSubmissionGroupFolderId(MoveFileTO moveFileTO) {
		logger.info("start verifyTaskSubmissionGroupFolderId");
		AppUser user = moveFileTO.getUser();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		Post task = moveFileTO.getPost();
		PostDAO postDAO = new PostDAO(em);
		if (null == task) {
			throw new ServiceException("task cannot be null for task submission");
		}
		Group group = CacheUtils.getGroup(task.getGroupId());
		String fileId = null;
		if (null == group) {
			throw new ServiceException("null group , groupId : " + task.getGroupId() + " , group ; " + group);
		}

		fileId = task.getGoogleDriveFolderId();
		GoogleDriveFile taskSubmissionFolder = StringUtils.isBlank(fileId) ? null : getFileFields(fileId, null, user);
		if (null == taskSubmissionFolder) {
			GroupMemberDAO groupMemberDAO = new GroupMemberDAO(em);
			GroupMember member = groupMemberDAO.fetchGroupMemberByEmail(moveFileTO.getGroupId(), user.getEmail());
			if (null == member) {
				throw new ServiceException(user.getEmail() + " is not member of group id :  " + moveFileTO.getGroupId());
			}
			String groupTaskSubmissionFolderId = member.getTaskSubmissionFolderId();
			GoogleDriveFile groupTaskSubmissionFolder = StringUtils.isBlank(groupTaskSubmissionFolderId) ? null : getFileFields(groupTaskSubmissionFolderId, null, user);
			if (null == groupTaskSubmissionFolder) {
				groupTaskSubmissionFolder = createNewFile(group.getName(), GoogleFileTypes.FOLDER, user);
				if (null == groupTaskSubmissionFolder) {
					throw new ServiceException("cannot make " + group.getName() + " groupTaskSubmission  folder for : " + user.getEmail());
				}
				groupTaskSubmissionFolderId = groupTaskSubmissionFolder.getId();
				moveFile(groupTaskSubmissionFolderId, user.getTaskSubmissionFolderId(), user);
				member.setTaskSubmissionFolderId(groupTaskSubmissionFolderId);
				groupMemberDAO.upsert(member);
			}

			taskSubmissionFolder = createNewFile(task.getTitle(), GoogleFileTypes.FOLDER, user);
			if (null == taskSubmissionFolder) {

				throw new ServiceException("cannot make " + task.getTitle() + " library folder for : " + user.getEmail());
			}
			fileId = taskSubmissionFolder.getId();
			moveFile(fileId, groupTaskSubmissionFolderId, user);
			Post db = postDAO.get(task.getId());
			db.setGoogleDriveFolderId(fileId);
			postDAO.upsert(db);
		}
		if (em.isOpen()) {
			em.close();
		}
		logger.info("exit verifyTaskSubmissionGroupFolderId, fileId : " + fileId);
		return fileId;
	}
	
	private String verifyLibraryGroupFolderId(MoveFileTO moveFileTO) {
		logger.info("start verifyLibraryGroupFolderId");
		AppUser user = moveFileTO.getUser();
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		GroupMemberDAO groupMemberDAO = new GroupMemberDAO(em);
		Group group = CacheUtils.getGroup(moveFileTO.getGroupId());
		String fileId = null;
		if (null == group) {
			throw new ServiceException("null group , groupId : " + moveFileTO.getGroupId() + " , group ; " + group);
		}
		GroupMember member = groupMemberDAO.fetchGroupMemberByEmail(moveFileTO.getGroupId(), user.getEmail());
		if(null == member){
			throw new ServiceException( user.getEmail() + " is not member of group id :  " + moveFileTO.getGroupId());
		}
		fileId = member.getLibraryFolderId();
		GoogleDriveFile libraryFolder = StringUtils.isBlank(fileId) ? null : getFileFields(fileId, null, user);
		if (null == libraryFolder) {
			libraryFolder = createNewFile(group.getName(), GoogleFileTypes.FOLDER, user);
			if (null == libraryFolder) {
				logger.error("cannot make " + group.getName() + " library folder for : " + user.getEmail());
			} 
			fileId = libraryFolder.getId();
			moveFile(fileId, user.getGoogleDriveLibraryFolderId(), user);
			member.setLibraryFolderId(fileId);
			groupMemberDAO.upsert(member);
			
		}
		if(em.isOpen()){
			em.close();
		}
		logger.info("exit verifyLibraryGroupFolderId, fileId : " + fileId);
		return fileId;
	}

	private String verifyPublicGroupFolderId(MoveFileTO moveFileTO ) {
		logger.info("start verifyPublicGroupFolderId");
		AppUser memberUser = moveFileTO.getUser();
		Long groupId = moveFileTO.getGroupId();
		Group group = CacheUtils.getGroup(groupId);
		String fileId = null;
		if (null == group) {
			throw new ServiceException("groupId : " + groupId + " , group ; " + group);
		} else {
			EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
			GroupService groupService = new GroupService(em);
			Group db = null;
			AppUser user = CacheUtils.getAppUser(group.getCreatedBy());
			if (null == user) {
				throw new ServiceException("null group , groupId : " + groupId + " , group.getCreatedBy() :  " + group.getCreatedBy() + " , user : " + user);
			}
			fileId = group.getFolderId();
			GoogleDriveFile file;
			boolean createFolder = true;
			if (StringUtils.isNotBlank(fileId)) {
				file = getFileFields(fileId, null, user);
				createFolder = (null == file);
			}

			if (createFolder) {
				file = createNewFile(group.getName(), GoogleFileTypes.FOLDER, user);
				if (null == file) {
					logger.error("cannot make " + group.getName() + " attachment folder for : " + user.getEmail());
				} else {
					fileId = file.getId();
					updatePermission(fileId, null, Constants.READER, Constants.ANYONE, "", true, false, user);
					moveFile(fileId, user.getGoogleDriveAttachmentsFolderId(), user);

					db = groupService.get(groupId);
					db.setFolderId(file.getId());
					groupService.upsert(db);
				}
			}

			if (StringUtils.isNotBlank(fileId)) {
				if (null == db) {
					db = groupService.get(groupId);
				}

				GoogleDriveFile postFolder = StringUtils.isBlank(group.getPostFolderId()) ? null : getFileFields(group.getPostFolderId(), null, user);

				if (null == postFolder) {
					postFolder = createNewFile(FOLDER.Post.toString(), GoogleFileTypes.FOLDER, user);
					moveFile(postFolder.getId(), fileId, user);
					db.setPostFolderId(postFolder.getId());
				}

				GoogleDriveFile taskFolder = StringUtils.isBlank(group.getTaskFolderId()) ? null : getFileFields(group.getTaskFolderId(), null, user);
				if (null == taskFolder) {
					taskFolder = createNewFile(FOLDER.Task.toString(), GoogleFileTypes.FOLDER, user);
					moveFile(taskFolder.getId(), fileId, user);
					db.setTaskFolderId(taskFolder.getId());
				}

				GoogleDriveFile scheduleFolder = StringUtils.isBlank(group.getScheduleFolderId()) ? null : getFileFields(group.getScheduleFolderId(), null, user);
				if (null == scheduleFolder) {
					scheduleFolder = createNewFile(FOLDER.Schedule.toString(), GoogleFileTypes.FOLDER, user);
					moveFile(scheduleFolder.getId(), fileId, user);
					db.setScheduleFolderId(scheduleFolder.getId());
				}

/*				GoogleDriveFile assignmentFolder = StringUtils.isBlank(group.getAssignmentFolderId()) ? null : getFileFields(group.getAssignmentFolderId(), null, user);
				if (null == assignmentFolder) {
					assignmentFolder = createNewFile(FOLDER.Assignment.toString(), GoogleFileTypes.FOLDER, user);
					moveFile(assignmentFolder.getId(), fileId, user);
					db.setAssignmentFolderId(assignmentFolder.getId());
				}*/

				groupService.upsert(db);

				if (!memberUser.getEmail().equals(user.getEmail())) {
					GoogleFilePermission permission = updatePermission(fileId, null, Constants.WRITER, Constants.USER, memberUser.getEmail(), false, false, user);
					if (StringUtils.isNotBlank(memberUser.getRefreshTokenAccountEmail()) && !memberUser.getRefreshTokenAccountEmail().equals(memberUser.getEmail())) {
						permission = updatePermission(fileId, null, Constants.WRITER, Constants.USER, memberUser.getRefreshTokenAccountEmail(), false, false, user);
					}
					
					if(null !=permission && StringUtils.isNotBlank(memberUser.getGoogleDriveAttachmentsFolderId())){
						moveFile(fileId, memberUser.getGoogleDriveAttachmentsFolderId(), memberUser);
					}
				}
			}

			if (em.isOpen()) {
				em.close();
			}
		}
		logger.info("exit verifyPublicGroupFolderId, fileId : " + fileId);
		return fileId;
	}

	private String verifyUserFolders(FOLDER folder, AppUser user) {
		logger.info("start verifyUserFolders, folder: " + folder + " , email : " + user.getEmail());
		String fileId = null;
		if (null != folder && null != user) {
			boolean createFolder = true;
			switch (folder) {
			case AllSchool:
				fileId = user.getGoogleDriveFolderId();
				break;
			case Attachments:
				fileId = user.getGoogleDriveAttachmentsFolderId();
				break;
			case Library:
				fileId = user.getGoogleDriveLibraryFolderId();
				break;
			case Task_Submission:
				fileId = user.getTaskSubmissionFolderId();
				break;
			case Tutorial:
				fileId = user.getTutorialFolderId();
				break;
			/*case Schedule:
				fileId = user.getScheduleFolderId();
				break;*/
			default:
				/* return as not valid for level 0 or 1 folders */
				return null;
			}
			GoogleDriveFile file;
			
			if (StringUtils.isNotBlank(fileId)) {
				file = getFileFields(fileId, null, user);
				createFolder = (null == folder);
			}

			if (createFolder) {
				file = createNewFile(folder.toString(), GoogleFileTypes.FOLDER, user);
				if (null == file) {
					throw new ServiceException("cannot make " + folder.toString() + " folder for : " + user.getEmail());
				} else {
					fileId = file.getId();
					EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
					AppUserService appUserService = new AppUserService(em);
					AppUser dbUser = appUserService.getAppUserByEmail(user.getEmail());
					switch (folder) {
					case AllSchool:
						dbUser.setGoogleDriveFolderId(fileId);
						user.setGoogleDriveFolderId(fileId);
						break;
					case Attachments:
						dbUser.setGoogleDriveAttachmentsFolderId(fileId);
						user.setGoogleDriveAttachmentsFolderId(fileId);
						break;
					case Library:
						dbUser.setGoogleDriveLibraryFolderId(fileId);
						user.setGoogleDriveLibraryFolderId(fileId);
						break;
					case Task_Submission:
						dbUser.setTaskSubmissionFolderId(fileId);
						user.setTaskSubmissionFolderId(fileId);
						break;
					case Tutorial:
						dbUser.setTutorialFolderId(fileId);
						user.setTutorialFolderId(fileId);
						updatePermission(fileId, null, Constants.READER, Constants.ANYONE, "", true, false, user);
						break;
					/*case Schedule:
						dbUser.setScheduleFolderId(fileId);
						user.setScheduleFolderId(fileId);
						break;*/
					default:
						return null;
					}
					appUserService.upsert(dbUser);

					List<FOLDER> level1 = Arrays.asList(new FOLDER[] { FOLDER.Attachments, FOLDER.Library , FOLDER.Task_Submission, FOLDER.Tutorial});
					/*List<FOLDER> attachmentLevels = Arrays.asList(new FOLDER[] { FOLDER.Schedule});*/
					if (FOLDER.AllSchool.equals(folder)) {
						// any changes for main folder
					} else if (level1.contains(folder)) {
						moveFile(fileId, user.getGoogleDriveFolderId(), user);
					}/* else if (attachmentLevels.contains(folder)) {
						moveFile(fileId, user.getGoogleDriveAttachmentsFolderId(), user);
					}*/
					if (em.isOpen()) {
						em.close();
					}
				}
			}

		}
		logger.info("exit verifyUserFolders, fileId : " + fileId);
		return fileId;
	}

}
