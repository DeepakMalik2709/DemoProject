package com.notes.nicefact.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.MultipartStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.Post;
import com.notes.nicefact.entity.Tutorial;
import com.notes.nicefact.enums.SHARING;
import com.notes.nicefact.google.GoogleAppUtils;
import com.notes.nicefact.to.AppUserTO;
import com.notes.nicefact.to.FileTO;
import com.notes.nicefact.to.TutorialTO;

public class Utils {
	public static final Logger logger = Logger.getLogger(Utils.class.getSimpleName());

	public static String encodeBase64(byte[] rawData) {
		String data = Base64.encodeBase64URLSafeString(rawData);
		return data;
	}

	public static String encodeBase64(String dataStr) {
		String data = null;
		try {
			data = encodeBase64(dataStr.getBytes(Constants.UTF_8));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return data;
	}

	public static String getMonthStr(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		String monthStr = new SimpleDateFormat("MMM").format(date) + new SimpleDateFormat("yyyy").format(date);
		return monthStr;
	}

	public static int getIntegerFromRequest(HttpServletRequest request, String paramName) {
		String val = getParamFromRequest(request, paramName);
		return NumberUtils.isNumber(val) ? Integer.parseInt(val) : -1;
	}

	public static String getParamFromRequest(HttpServletRequest request, String paramName) {
		String val = request.getParameter(paramName);
		return StringUtils.isBlank(val) ? "" : val;
	}

	public static String[] getParamArrayFromRequest(HttpServletRequest request, String paramName) {
		String[] val = request.getParameterValues(paramName);
		if (val != null && val.length == 1 && val[0].contains(",")) {
			val = val[0].split(",");
		}
		return val;
	}

	public static String getDomain(String email) {
		return email != null ? email.substring(email.indexOf('@') + 1) : null;
	}

	public static String getUserNameFromEmail(String emailAddr) {
		return emailAddr.substring(0, emailAddr.indexOf("@"));
	}

	public static List<String> stringToList(String string, String delimiter) {
		List<String> ipFilters = new ArrayList<>();
		if (StringUtils.isNotBlank(string) && StringUtils.isNotBlank(delimiter)) {
			String[] array = string.split(delimiter);
			ipFilters.addAll(Arrays.asList(array));
		}
		return ipFilters;
	}

	public static String listToString(List<String> list, String delimiter) {
		StringBuffer sb = new StringBuffer();
		if (list != null && !list.isEmpty()) {
			for (String string : list) {
				sb.append(string);
				sb.append(delimiter);
			}
		}
		return sb.toString();
	}

	public static boolean isValidEmailAddress(String email) {
		boolean isValid = false;
		if (StringUtils.isNotEmpty(email)) {
			String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
			CharSequence inputStr = email;
			Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(inputStr);
			if (matcher.matches()) {
				isValid = true;
			}
		}
		return isValid;
	}

	public static String getTemplateFilePath(String fileName) {
		String lang = "en";
		String path = "mailTemplate/" + fileName + "_" + lang + ".vm";
		return path;
	}

	public static List<TutorialTO> adaptTutorialTO(List<Tutorial> tutorials) {
		List<TutorialTO> tutorialTos = new ArrayList<>();
		for (Tutorial tutorial : tutorials) {
			TutorialTO to = new TutorialTO(tutorial);
			tutorialTos.add(to);
		}
		return tutorialTos;
	}

	public static boolean userHasPermission(Post post, AppUser appUser) {
		if (SHARING.PUBLIC.equals(post.getSharing())) {
			return true;
		} else if (SHARING.GROUP.equals(post.getSharing()) && appUser.getGroupIds().contains(post.getId())) {
			return true;
		}
		return false;
	}

	private static String getMimeTypeFromHeader(final String header) {
		String mimeType = "application/octet-stream";
		if (header.contains("Content-Type")) {
			mimeType = header.substring(header.indexOf("Content-Type") + 13).trim();
		}
		return mimeType;
	}

	public static String getFileNameFromHeader(final String header) {
		String filename = "temp";
		if (header.contains("Content-Disposition")) {
			filename = header.substring(header.indexOf("filename=") + 10);
			if (filename.contains("\"")) {
				filename = filename.substring(0, filename.indexOf("\""));
			}
			filename = filename.trim();
		}
		return filename;
	}

	public static List<FileTO> writeFilesToTempfolder(HttpServletRequest request) {
		List<FileTO> files = new ArrayList<>();
		FileTO fileTO = null;
		try {
			String contentType = request.getContentType();
			String boundary = contentType.substring(contentType.indexOf("boundary=") + 9);
			MultipartStream multipartStream = new MultipartStream(request.getInputStream(), boundary.getBytes(Constants.UTF_8));
			boolean nextPart = multipartStream.skipPreamble();
			ByteArrayOutputStream output = null;
			String fileName = null;
			String mimeType = null;
			String filePath = null;
			String serverName = null;
			Path tempPath = Paths.get(AppProperties.getInstance().getTempUploadsFolder());
			if (Files.notExists(tempPath)) {
				Files.createDirectories(tempPath);
			}
			while (nextPart) {
				String header = multipartStream.readHeaders();
				/* process headers , DO NOT REMOVE , UPLOAD STOPS WORKING */
				logger.info(header);
				output = new ByteArrayOutputStream();
				fileName = getFileNameFromHeader(header);
				mimeType = getMimeTypeFromHeader(header);
				multipartStream.readBodyData(output);
				nextPart = multipartStream.readBoundary();
				byte[] fileBytes = output.toByteArray();
				serverName = new Random().nextInt(1000) + "_" + new Date().getTime() + "_" + fileName;
				filePath = AppProperties.getInstance().getTempUploadsFolder() + serverName;
				java.nio.file.Path output1 = Paths.get(filePath);

				Files.write(output1, fileBytes, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
				fileTO = new FileTO(fileName, serverName, mimeType, fileBytes.length);
				files.add(fileTO);
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return files;
	}

	public static FileTO writeTutorialFileThumbnail(byte[] fileBytes, String email, String fileName) {
		String fileBasePath = AppProperties.getInstance().getTutorialUploadsFolder()+ email + File.separator + Constants.THUMBNAIL_FOLDER;
		return writeFileToPath(fileBytes, fileBasePath, fileName);
	}
	
	public static FileTO writeGroupPostFileThumbnail(byte[] fileBytes, Long groupId, String fileName) {
		String fileBasePath = AppProperties.getInstance().getGroupUploadsFolder() + groupId + File.separator + Constants.THUMBNAIL_FOLDER;
		return writeFileToPath(fileBytes, fileBasePath, fileName);
	}

	public static FileTO writeFileToPath(byte[] fileBytes, String fileBasePath, String fileName) {
		FileTO fileTO = null;
		try {
			if (Files.notExists(Paths.get(fileBasePath))) {
				Files.createDirectories(Paths.get(fileBasePath));
			}

			String serverName = fileName + "_" + new Random().nextInt(1000) + "_" + new Date().getTime() + ".png";
			String filePath = fileBasePath + serverName;
			java.nio.file.Path output1 = Paths.get(filePath);

			Files.write(output1, fileBytes, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
			fileTO = new FileTO(fileName, filePath, null, fileBytes.length);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return fileTO;
	}

	public static String readableFileSize(long size) {
		if (size <= 0)
			return "0";
		final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

	public static byte[] readFileBytes(String filePath) {
		try {
			Path path = Paths.get(filePath);
			if (Files.exists(path)) {
				return Files.readAllBytes(path);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}


	public static String n2b(String str) {
		if (str == null || "null".equals(str)) {
			return "";
		}
		return str.trim();
	}

	public static String getAccessToken() {
		String token = CurrentContext.getAppUser().getAccessToken();
		if (StringUtils.isBlank(token)) {
			token = refreshAccessToken();
		}
		return token;
	}

	public static void revokeToken(String refreshToken){
		try {
			HttpClient client = HttpClients.createDefault();
			List<Header> headers = new ArrayList<>();
			headers.add(new BasicHeader("Content-Type", "application/x-www-form-urlencoded"));
			String url = "https://accounts.google.com/o/oauth2/revoke?token=" + refreshToken;
				int i = 0;
				HttpGet request1 = new HttpGet(url);
				logger.info("attempt : " + i);
				if (headers != null) {
					for (Header header : headers) {
						request1.addHeader(header);
					}
				}
				HttpResponse response = client.execute(request1);
				logger.warn("resp : " + response.getStatusLine().getStatusCode());
				if (response.getEntity() != null) {
					String respStr = new String(IOUtils.toByteArray(response.getEntity().getContent()), Constants.UTF_8);
					logger.info(respStr);
				}
				
				request1.releaseConnection();

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
	
	public static String refreshAccessToken() {
		String token = null;
		try {
			List<Header> headers = new ArrayList<>();
			headers.add(new BasicHeader("Content-Type", "application/x-www-form-urlencoded"));

			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("grant_type", "refresh_token"));
			nameValuePairs.add(new BasicNameValuePair("refresh_token", CurrentContext.getAppUser().getRefreshToken()));
			nameValuePairs.add(new BasicNameValuePair("client_id", AppProperties.getInstance().getGoogleClientId()));
			nameValuePairs.add(new BasicNameValuePair("client_secret", AppProperties.getInstance().getGoogleClientSecret()));

			try {
				HttpEntity entity = new UrlEncodedFormEntity(nameValuePairs);
				JSONObject jsonObject = Utils.makeJsonPostRequest(Constants.GOOGLE_OAUTH_TOKEN_URL, headers, entity);
				if (null != jsonObject && jsonObject.has("access_token")) {
					token = jsonObject.getString("access_token");
					CurrentContext.getAppUser().setAccessToken(token);
					GoogleAppUtils.getCredential().setAccessToken(token);
				}

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return token;
	}

	public static JSONObject makeJsonPostRequest(String url, List<Header> headers, HttpEntity body) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		if (null != headers) {
			for (Header header : headers) {
				httpPost.addHeader(header);
			}
		}
		if (null != body) {
			httpPost.setEntity(body);
		}
		JSONObject json = null;
		logger.info("url : " + url);
		for (int i = 0; i < 4; i++) {
			try {
				HttpResponse response = httpclient.execute(httpPost);
				int statusCode = response.getStatusLine().getStatusCode();
				logger.info("response code : " + statusCode);
				HttpEntity entity = response.getEntity();
				if (entity != null) {

					if (statusCode != 200) {
						if (entity.getContent() != null) {
							String respStr = new String(IOUtils.toByteArray(entity.getContent()), Constants.UTF_8);
							logger.warn("error : \n" + respStr);
						} else {
							logger.warn(" No response for " + url);
						}
						Thread.sleep((i * 1000) + new Random().nextInt(1000));
					} else {
						String respStr = new String(IOUtils.toByteArray(entity.getContent()), Constants.UTF_8);
						json = new JSONObject(respStr);
						break;
					}

				} else {
					logger.warn("entity is null for : " + url);
					Thread.sleep((i * 1000) + new Random().nextInt(1000));
				}

			} catch (IOException e) {
				e.printStackTrace();
				break;
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return json;
	}

	// for drive api
	public static Date getModifiedDate(String modifiedDate, Integer tZShift) {

		Date d = null;
		if (null != modifiedDate) {
			final String pattern = "yyyy-MM-dd'T'hh:mm:ss";
			final SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			try {
				d = sdf.parse(modifiedDate);
				d = new Date(d.getTime() + tZShift);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return d;
	}

	public static String cutStr(String str, int length) {
		StringBuilder result = new StringBuilder();
		int cnt = 0;
		int destLength = 0;
		try {
			while (true) {
				char c = str.charAt(cnt);
				if (Character.getType(c) == 5) {
					destLength += 2;
				} else {
					destLength++;
				}

				result.append(c);
				if (destLength >= length || cnt >= length) {
					// result.append("..");
					break;
				}
				cnt++;
			}
		} catch (Exception e) {
			if (e.getMessage().indexOf("index out of range") > 0) {
				return result.toString();
			}
		}

		return result.toString();
	}

	public static void refreshToken(AppUser user) {
		logger.info("enter refreshToken");
		if (StringUtils.isBlank(user.getRefreshToken())) {
			logger.warn(" refresh token is null for : " + user.getEmail());
		}else{
			HttpClient client = HttpClients.createDefault();
			String url = Constants.GOOGLE_OAUTH_TOKEN_URL;
			HttpPost request1 = new HttpPost(url);
			request1.addHeader("Content-Type", "application/x-www-form-urlencoded");
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("grant_type", "refresh_token"));
			nameValuePairs.add(new BasicNameValuePair("client_id", AppProperties.getInstance().getGoogleClientId()));
			nameValuePairs.add(new BasicNameValuePair("client_secret", AppProperties.getInstance().getGoogleClientSecret()));
			nameValuePairs.add(new BasicNameValuePair("refresh_token", user.getRefreshToken()));
			try {
				request1.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response1 = client.execute(request1);
				if (response1.getStatusLine().getStatusCode() == 200) {
					byte[] bytes = IOUtils.toByteArray(response1.getEntity().getContent());
					JSONObject json = new JSONObject(new String(bytes, Constants.UTF_8));
					if (json.has("access_token")) {
						String newAccesstoken = json.getString("access_token");
						logger.info("new access token : " + newAccesstoken);
						user.setAccessToken(newAccesstoken);
						GoogleAppUtils.getCredential().setAccessToken(newAccesstoken);
						CacheUtils.addUserToCache(user);
					}
				} else if (null != response1.getEntity()) {
					byte[] bytes = IOUtils.toByteArray(response1.getEntity().getContent());
					String msg = new String(bytes, Constants.UTF_8);
					logger.error(msg);
				}
			} catch (IOException | JSONException e) {
				logger.error("error : " + e.getMessage(), e);
			}
			request1.releaseConnection();
		}
		logger.info("exit refreshToken");

	}

	public static String getGoolgeServiceAccountToken() {
		String accessToken = null;
		try {

			String serviceAccountEmail = AppProperties.getInstance().getGoogleServiceAccountEmail();
			String cacheKey = CacheUtils.generateUserKey(serviceAccountEmail);
			AppUser hr = (AppUser) CacheUtils.getFromCache(cacheKey);
			if (hr == null || (new Date().getTime() - hr.getCreatedTime().getTime() > 3500000)) {
				String pkFilePath = AppProperties.getInstance().getGoogleServiceAccountPKFilePath(); // "E:/WorkSpace/Notes/WebContent/WEB-INF/GPSTracker-8b13a75942d0.p12"
				String serviceAccountPassword = AppProperties.getInstance().getGoogleServiceAccountPassword();
				String header = "{\"alg\":\"RS256\",\"typ\":\"JWT\"}";
				String claimTemplate = "'{'\"iss\": \"{0}\", \"scope\": \"{1}\", \"aud\": \"{2}\", \"exp\": {3}, \"iat\": {4}'}'";
				StringBuffer token = new StringBuffer();

				// Encode the JWT Header and add it to our string to sign
				token.append(encodeBase64(header.getBytes("UTF-8")));

				// Separate with a period
				token.append(".");

				long iat = (System.currentTimeMillis() / 1000);
				long exp = iat + 3600;
				// Create the JWT Claims Object
				String[] claimArray = new String[6];

				logger.info(" serviceAccountEmail: " + serviceAccountEmail + ", pkFilePath: " + pkFilePath);

				claimArray[0] = serviceAccountEmail;
				claimArray[1] = Constants.GOOGLE_SERVICE_ACCOUNT_SCOPES;
				claimArray[2] = "https://accounts.google.com/o/oauth2/token";
				claimArray[3] = "" + exp;
				claimArray[4] = "" + iat;
				MessageFormat claims = new MessageFormat(claimTemplate);
				String payload = claims.format(claimArray);
				// Add the encoded claims object
				token.append(encodeBase64(payload.getBytes(Constants.UTF_8)));
				// Load the private key
				PrivateKey privateKey = getPrivateKey(pkFilePath, serviceAccountPassword);
				byte[] sig = signData(token.toString().getBytes(Constants.UTF_8), privateKey);

				String signedPayload = encodeBase64(sig);

				// Separate with a period
				token.append(".");

				// Add the encoded signature
				token.append(signedPayload);
				// accessToken = CurrentContext.getLoginUser().getAccessToken();
				accessToken = makeRequestFor2LOAccessToken(token.toString());
				hr = new AppUser();
				hr.setEmail(serviceAccountEmail);
				hr.setAccessToken(accessToken);
				hr.setCreatedTime(new Date());
				CacheUtils.addUserToCache(hr);
			} else {
				accessToken = hr.getAccessToken();
			}
		} catch (Exception e) {
			logger.error("error : " + e.getMessage(), e);
		}
		return accessToken;
	}

	private static String makeRequestFor2LOAccessToken(String assertion) throws JSONException, MalformedURLException {
		String newAccesstoken = null;
		HttpClient client = HttpClients.createDefault();
		String url = Constants.GOOGLE_OAUTH_TOKEN_URL;
		HttpPost request1 = new HttpPost(url);
		request1.addHeader("Content-Type", "application/x-www-form-urlencoded");

		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
		nameValuePairs.add(new BasicNameValuePair("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer"));
		nameValuePairs.add(new BasicNameValuePair("assertion", assertion));
		try {
			request1.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response1 = client.execute(request1);
			if (response1.getStatusLine().getStatusCode() == 200) {
				byte[] bytes = IOUtils.toByteArray(response1.getEntity().getContent());
				JSONObject json = new JSONObject(new String(bytes, Constants.UTF_8));
				if (json.has("access_token")) {
					newAccesstoken = json.getString("access_token");
					logger.info("new access token : " + newAccesstoken);
				}
			} else if (null != response1.getEntity()) {
				byte[] bytes = IOUtils.toByteArray(response1.getEntity().getContent());
				String msg = new String(bytes, Constants.UTF_8);
				logger.error(msg);
			}
		} catch (IOException | JSONException e) {
			logger.error("error : " + e.getMessage(), e);
		}
		request1.releaseConnection();

		return newAccesstoken;
	}

	private static PrivateKey getPrivateKey(String keyFile, String password) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException {
		String keyAlias = "privatekey";
		KeyStore keystore = KeyStore.getInstance("PKCS12");
		keystore.load(new FileInputStream(ClassPath.getInstance().getWebInfPath() + keyFile), password.toCharArray());
		PrivateKey privateKey = (PrivateKey) keystore.getKey(keyAlias, password.toCharArray());

		return privateKey;
	}

	public static byte[] signData(byte[] data, PrivateKey privateKey) throws InvalidKeyException, SignatureException, NoSuchAlgorithmException {
		Signature signature = Signature.getInstance("SHA256withRSA");
		signature.initSign(privateKey);
		signature.update(data);
		return signature.sign();
	}

	public static String refreshServiceAccoungToken() {
		String serviceAccountEmail = AppProperties.getInstance().getGoogleServiceAccountEmail();
		String cacheKey = CacheUtils.generateUserKey(serviceAccountEmail);
		CacheUtils.removeFromCache(cacheKey);
		return getGoolgeServiceAccountToken();
	}

	public static void updateAppUserFromTo(AppUser appUser, AppUserTO appUserTO) {
		if (StringUtils.isNotBlank(appUserTO.getFirstName())) {
			appUser.setFirstName(appUserTO.getFirstName());
		}
		if (StringUtils.isNotBlank(appUserTO.getLastName())) {
			appUser.setLastName(appUserTO.getLastName());
		}
		if (StringUtils.isNotBlank(appUserTO.getPassword())) {
			appUser.setPassword(appUserTO.getPassword());
		}
		if (null != appUserTO.getSendGroupPostEmail()) {
			appUser.setSendGroupPostEmail(appUserTO.getSendGroupPostEmail());
		}
		if (null != appUserTO.getSendGroupPostMentionEmail()) {
			appUser.setSendGroupPostMentionEmail(appUserTO.getSendGroupPostMentionEmail());
		}
		if (null != appUserTO.getSendPostCommentedEmail()) {
			appUser.setSendPostCommentedEmail(appUserTO.getSendPostCommentedEmail());
		}
		if (null != appUserTO.getSendCommentMentiondEmail()) {
			appUser.setSendCommentMentiondEmail(appUserTO.getSendCommentMentiondEmail());
		}
		if (null != appUserTO.getSendCommentOnMentiondPostEmail()) {
			appUser.setSendCommentOnMentiondPostEmail(appUserTO.getSendCommentOnMentiondPostEmail());
		}
		if (null != appUserTO.getSendCommentReplyEmail()) {
			appUser.setSendCommentReplyEmail(appUserTO.getSendCommentReplyEmail());
		}
		if (null != appUserTO.getSendCommentOnCommentEmail()) {
			appUser.setSendCommentOnCommentEmail(appUserTO.getSendCommentOnCommentEmail());
		}

	}

	public static String getRandomColor() {			
		String letters = "0123456789ABCDEF";
		String color = "#";
		Random rand = new Random();
			  for (int i = 0; i < 6; i++) {
				  
			    color += letters.charAt(rand.nextInt(16));
			  }
			
		return color;
	}
	
	public static String getTaskFolderPath(Post task) {	
		return AppProperties.getInstance().getGroupUploadsFolder() + task.getGroupId() +  File.separator + task.getId();
	}

}
