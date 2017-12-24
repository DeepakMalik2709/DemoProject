package com.notes.nicefact.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
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

import com.google.api.services.calendar.CalendarScopes;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.entity.AppUser.AUTHORIZED_SCOPES;
import com.notes.nicefact.entity.AppUser.GENDER;
import com.notes.nicefact.exception.AppException;
import com.notes.nicefact.service.AppUserService;
import com.notes.nicefact.service.BackendTaskService;
import com.notes.nicefact.to.AppUserTO;
import com.notes.nicefact.util.AppProperties;
import com.notes.nicefact.util.CacheUtils;
import com.notes.nicefact.util.Constants;
import com.notes.nicefact.util.CurrentContext;
import com.notes.nicefact.util.EntityManagerHelper;
import com.notes.nicefact.util.Utils;

@Path("/oauth")
public class OauthController {


	static Logger logger = Logger.getLogger(OauthController.class.getSimpleName());

	public static final String GOOGLE_AUTH_URL = "https://accounts.google.com/o/oauth2/auth";

	public static final String GOOGLE_OAUTH_TOKEN_URL = "https://accounts.google.com/o/oauth2/token";

	public static final String GOOGLE_SCOPES = "https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile ";

	public static final String GOOGLE_CALLBACK = "googleCallback";
	
	public static final String GOOGLE_DRIVE_CALLBACK = "driveCallback";

	public static final String FACEBOOK_AUTH_URL = "https://www.facebook.com/dialog/oauth";

	public static final String FACEBOOK_OAUTH_TOKEN_URL = "https://graph.facebook.com/oauth/access_token";

	public static final String FACEBOOK_SCOPES = "public_profile,email";

	public static final String FACEBOOK_CALLBACK = "facebookCallback";

	@GET
	@Path(GOOGLE_DRIVE_CALLBACK)
	public void googleDriveCallback(@QueryParam("code") String code, @QueryParam("error") String error, @Context HttpServletResponse response, @Context HttpServletRequest request) throws IOException,
			JSONException {
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			logger.info("start googleDriveCallback : " + request.getQueryString());
			if (StringUtils.isNotBlank(code)) {
				AppUserService appUserService = new AppUserService(em);
				BackendTaskService backendTaskService = new BackendTaskService(em);
				AppUser sessionUser = (AppUser) request.getSession().getAttribute(Constants.SESSION_KEY_lOGIN_USER);
				AppUser user = appUserService.getAppUserByEmail(sessionUser.getEmail());
				String allScopes = request.getParameter("scope").replaceAll("\\+", " ");
				getRefreshTokenFromAuthorizatoinCode(allScopes, "/a/oauth/" + GOOGLE_DRIVE_CALLBACK, code, user);
				if (StringUtils.isNotBlank(user.getRefreshToken())) {
					
					if (allScopes.contains(CalendarScopes.CALENDAR)) {
						user.getScopes().add(AUTHORIZED_SCOPES.CALENDAR);
					}
					if (allScopes.contains(Constants.GOOGLE_DRIVE_SCOPES)) {
						user.getScopes().add(AUTHORIZED_SCOPES.DRIVE);
					}

					AppUserTO userTo = getGoogleUserProfile(user.getAccessToken());
					user.setRefreshTokenAccountEmail(userTo.getEmail());
					appUserService.upsert(user);
					CacheUtils.addUserToCache(user);
					request.getSession().setAttribute(Constants.SESSION_KEY_lOGIN_USER, user);
					if (allScopes.contains(Constants.GOOGLE_DRIVE_SCOPES)) {
						backendTaskService.createGoogleDriveFolderForUserTask(user);
					}
				}

			} else {
				CurrentContext.getCommonContext().setMessage("Google Drive could not be enabled for your account. Please try again.");
				logger.error("message : " + error);
			}
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
		response.sendRedirect(Constants.PROFILE_PAGE);
		logger.info("exit googleDriveCallback ");
	}

	@GET
	@Path("/driveAuthorization")
	public Response driveAuthorization(@Context HttpServletResponse response) throws IOException, URISyntaxException {
		String url = GOOGLE_AUTH_URL + "?scope=" + URLEncoder.encode(Constants.GOOGLE_DRIVE_SCOPES + Constants.PROFILE_SCOPES, Constants.UTF_8) + "&include_granted_scopes=true&response_type=code&access_type=offline&approval_prompt=force" + "&client_id="
				+ AppProperties.getInstance().getGoogleClientId() + "&redirect_uri="
				+ URLEncoder.encode(AppProperties.getInstance().getApplicationUrl() + "/a/oauth/" + GOOGLE_DRIVE_CALLBACK, Constants.UTF_8);
		return Response.seeOther(new URI(url)).build();
	}
	
	@GET
	@Path("/calendarAuthorization")
	public Response calendarAuthorization(@Context HttpServletResponse response) throws IOException, URISyntaxException {
		String url = GOOGLE_AUTH_URL + "?scope=" + URLEncoder.encode(CalendarScopes.CALENDAR  + Constants.PROFILE_SCOPES , Constants.UTF_8) + "&include_granted_scopes=true&response_type=code&access_type=offline&approval_prompt=force" + "&client_id="
				+ AppProperties.getInstance().getGoogleClientId() + "&redirect_uri="
				+ URLEncoder.encode(AppProperties.getInstance().getApplicationUrl() + "/a/oauth/" + GOOGLE_DRIVE_CALLBACK, Constants.UTF_8);
		return Response.seeOther(new URI(url)).build();
	}
	
	@GET
	@Path("/googleAllAuthorization")
	public Response googleAuthorization(@Context HttpServletResponse response) throws IOException, URISyntaxException {
		String allScopes = Constants.GOOGLE_DRIVE_SCOPES +" " + CalendarScopes.CALENDAR   + Constants.PROFILE_SCOPES ;
		String url = GOOGLE_AUTH_URL + "?scope=" + URLEncoder.encode(allScopes, Constants.UTF_8) + "&include_granted_scopes=true&response_type=code&access_type=offline&approval_prompt=force" + "&client_id="
				+ AppProperties.getInstance().getGoogleClientId() + "&redirect_uri="
				+ URLEncoder.encode(AppProperties.getInstance().getApplicationUrl() + "/a/oauth/" + GOOGLE_DRIVE_CALLBACK, Constants.UTF_8);
		return Response.seeOther(new URI(url)).build();
	}
	

	
	@GET
	@Path(GOOGLE_CALLBACK)
	public void googleCallback(@QueryParam("code") String code, @QueryParam("error") String error, @Context HttpServletResponse response, @Context HttpServletRequest request) throws IOException,
			JSONException {
		if (StringUtils.isNotBlank(code)) {
			String accessToken = getGoogleAccessTokenFromAuthorizatoinCode(GOOGLE_SCOPES, "/a/oauth/" + GOOGLE_CALLBACK, code);
			AppUserTO userTo = getGoogleUserProfile(accessToken);			
			AppUser user = doAutoLogin(userTo, request,accessToken);
			String recirectUrl = Constants.HOME_PAGE ;
			if(!user.getUseGoogleDrive()){
				recirectUrl ="/a/oauth/googleAllAuthorization";
			}else if(null != CurrentContext.getCommonContext() && StringUtils.isNotBlank(CurrentContext.getCommonContext().getRedirectUrl())){
				recirectUrl = CurrentContext.getCommonContext().getRedirectUrl();
			}
			response.sendRedirect(recirectUrl);
		} else {
			logger.error("message : " + error);
		}
	}

	@GET
	@Path("/googleLogin")
	public Response googleLogin(@Context HttpServletResponse response , @QueryParam("redirect")final String redirect) throws IOException, URISyntaxException {
		if(CurrentContext.getAppUser() == null || !AppProperties.getInstance().isProduction()){
			CurrentContext.getCommonContext().setRedirectUrl(redirect);
			String url = GOOGLE_AUTH_URL + "?scope=" + URLEncoder.encode(GOOGLE_SCOPES, Constants.UTF_8) + "&response_type=code&access_type=online&approval_prompt=force" + "&client_id="
					+ AppProperties.getInstance().getGoogleClientId() + "&redirect_uri="
					+ URLEncoder.encode(AppProperties.getInstance().getApplicationUrl() + "/a/oauth/" + GOOGLE_CALLBACK, Constants.UTF_8);
			return Response.seeOther(new URI(url)).build();
		}else{
			String recirectUrl = Constants.HOME_PAGE ;
			if(StringUtils.isNotBlank(redirect)){
				recirectUrl = redirect;
			}
			response.sendRedirect(recirectUrl);
			return null;
		}
		
	/*	String url = GOOGLE_AUTH_URL + "?scope=" + URLEncoder.encode(GOOGLE_SCOPES, Constants.UTF_8) + "&response_type=code&access_type=online&approval_prompt=force" + "&client_id="
				+ AppProperties.getInstance().getGoogleClientId() + "&redirect_uri="
				+ URLEncoder.encode(AppProperties.getInstance().getApplicationUrl() + "/a/oauth/" + GOOGLE_CALLBACK, Constants.UTF_8);
		return Response.seeOther(new URI(url)).build();*/
	}

	public static void getRefreshTokenFromAuthorizatoinCode(String scope, String callback, String code, AppUser user) {

		List<Header> headers = new ArrayList<>();
		headers.add(new BasicHeader("Content-Type", "application/x-www-form-urlencoded"));

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("scope", scope));
		nameValuePairs.add(new BasicNameValuePair("grant_type", "authorization_code"));
		nameValuePairs.add(new BasicNameValuePair("client_id", AppProperties.getInstance().getGoogleClientId()));
		nameValuePairs.add(new BasicNameValuePair("client_secret", AppProperties.getInstance().getGoogleClientSecret()));
		nameValuePairs.add(new BasicNameValuePair("redirect_uri", AppProperties.getInstance().getApplicationUrl() + callback));
		nameValuePairs.add(new BasicNameValuePair("code", code));

		try {
			HttpEntity entity = new UrlEncodedFormEntity(nameValuePairs);

			JSONObject jsonObject = makePostRequest(GOOGLE_OAUTH_TOKEN_URL, headers, entity);
			if (null != jsonObject ){
				if( jsonObject.has("access_token")) {
					String token = jsonObject.getString("access_token");
					user.setAccessToken(token);
				}
				
				if( jsonObject.has("refresh_token")) {
					String token = jsonObject.getString("refresh_token");
					user.setRefreshToken(token);
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	
	public static String getGoogleAccessTokenFromAuthorizatoinCode(String scope, String callback, String code) {
		String token = null;

		List<Header> headers = new ArrayList<>();
		headers.add(new BasicHeader("Content-Type", "application/x-www-form-urlencoded"));

		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("scope", scope));
		nameValuePairs.add(new BasicNameValuePair("grant_type", "authorization_code"));
		nameValuePairs.add(new BasicNameValuePair("client_id", AppProperties.getInstance().getGoogleClientId()));
		nameValuePairs.add(new BasicNameValuePair("client_secret", AppProperties.getInstance().getGoogleClientSecret()));
		nameValuePairs.add(new BasicNameValuePair("redirect_uri", AppProperties.getInstance().getApplicationUrl() + callback));
		nameValuePairs.add(new BasicNameValuePair("code", code));

		try {
			HttpEntity entity = new UrlEncodedFormEntity(nameValuePairs);

			JSONObject jsonObject = makePostRequest(GOOGLE_OAUTH_TOKEN_URL, headers, entity);
			if (null != jsonObject && jsonObject.has("access_token")) {
				token = jsonObject.getString("access_token");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return token;
	}

	private AppUser doAutoLogin(AppUserTO userTo, HttpServletRequest request, String accessToken) {
		EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
		try {
			AppUserService appUserService = new AppUserService(em);
			AppUser user = appUserService.getAppUserByEmail(userTo.getEmail());
			if (null == user) {
				user = appUserService.registerNewUser(userTo);
			} else {
				appUserService.updatePublicDetails(user, userTo);
			}
			if(StringUtils.isNotBlank(user.getRefreshToken())){
				Utils.refreshToken(user);
			}else{
				user.setAccessToken(accessToken);
			}
			CacheUtils.addUserToCache(user);
			request.getSession().setAttribute(Constants.SESSION_KEY_lOGIN_USER, user);
			return user;
		} finally {
			if (em.isOpen()) {
				em.close();
			}
		}
	}

	public static HttpEntity makeGetRequest(String url, List<Header> headers) {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		if (null != headers) {
			for (Header header : headers) {
				httpGet.addHeader(header);
			}
		}
		logger.info("url : " + url);
		for (int i = 0; i < 4; i++) {
			try {
				HttpResponse response = httpclient.execute(httpGet);
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
						// String respStr = new
						// String(IOUtils.toByteArray(entity.getContent()),
						// Constants.UTF_8);
						return entity;
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
			}
		}
		return null;
	}

	public static JSONObject makeJsonGetRequest(String url, List<Header> headers) {

		JSONObject json = null;
		HttpEntity entity = makeGetRequest(url, headers);
		if (null != entity) {
			try {
				String respStr = new String(IOUtils.toByteArray(entity.getContent()), Constants.UTF_8);
				json = new JSONObject(respStr);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedOperationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return json;
	}

	public static JSONObject makePostRequest(String url, List<Header> headers, HttpEntity body) {
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

	@GET
	@Path("facebookLogin")
	public Response facebookLogin() throws IOException, URISyntaxException {
		String url = FACEBOOK_AUTH_URL + "?scope=" + URLEncoder.encode(FACEBOOK_SCOPES, Constants.UTF_8) + "&response_type=code&access_type=online&approval_prompt=force" + "&client_id="
				+ AppProperties.getInstance().getFacebookclientid() + "&redirect_uri="
				+ URLEncoder.encode(AppProperties.getInstance().getApplicationUrl() + "/a/oauth/" + FACEBOOK_CALLBACK, Constants.UTF_8);
		;
		return Response.seeOther(new URI(url)).build();
	}

	@GET
	@Path(FACEBOOK_CALLBACK)
	public void facebookCallback(@QueryParam("code") String code, @QueryParam("error") String error, @Context HttpServletResponse response, @Context HttpServletRequest request) throws IOException,
			JSONException {
		if (StringUtils.isNotBlank(code)) {
			String accessToken = getFacebookAccessTokenFromAuthorizatoinCode(FACEBOOK_SCOPES, "/a/oauth/" + FACEBOOK_CALLBACK, code);
			AppUserTO userTo = getFacebookUserProfile(accessToken);

			doAutoLogin(userTo, request,accessToken);
			String recirectUrl = Constants.HOME_PAGE ;
			if(null != CurrentContext.getCommonContext() && StringUtils.isNotBlank(CurrentContext.getCommonContext().getRedirectUrl())){
				recirectUrl = CurrentContext.getCommonContext().getRedirectUrl();
			}
			response.sendRedirect(recirectUrl);
		} else {
			logger.error("message : " + error);
		}
	}

	public static String getFacebookAccessTokenFromAuthorizatoinCode(String scope, String callback, String code) {
		String token = null;
		try {
			String url = FACEBOOK_OAUTH_TOKEN_URL + "?" + "&client_id=" + AppProperties.getInstance().getFacebookclientid() + "&client_secret=" + AppProperties.getInstance().getFacebookclientsecret()
					+ "&redirect_uri=" + URLEncoder.encode(AppProperties.getInstance().getApplicationUrl() + callback, Constants.UTF_8) + "&code=" + code;
			HttpEntity entity = makeGetRequest(url, null);
			if (entity != null) {
				String respStr = new String(IOUtils.toByteArray(entity.getContent()), Constants.UTF_8);
				JSONObject json = new JSONObject(respStr);
				if(json.has("access_token")){
					token = json.getString("access_token");
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return token;
	}

	public static AppUserTO getGoogleUserProfile(String accessToken) {
		AppUserTO userTo = new AppUserTO();
		List<Header> headers = new ArrayList<>();
		headers.add(new BasicHeader("Authorization", "Bearer " + accessToken));
		String url = "https://www.googleapis.com/oauth2/v1/userinfo?alt=json";
		JSONObject jsonObject = makeJsonGetRequest(url, headers);
		if (null == jsonObject) {
			throw new AppException("Could not fetch user's Google profile");
		}

		try {
			if (jsonObject.has("email")) {
				userTo.setEmail(jsonObject.getString("email"));
			}
			if (jsonObject.has("name")) {
				userTo.setDisplayName(jsonObject.getString("name"));
			}
			if (jsonObject.has("given_name")) {
				userTo.setFirstName(jsonObject.getString("given_name"));
			}
			if (jsonObject.has("family_name")) {
				userTo.setLastName(jsonObject.getString("family_name"));
			}
			if (jsonObject.has("picture")) {
				userTo.setPhotoUrl(jsonObject.getString("picture"));
			}
			if (jsonObject.has("gender")) {
				if (jsonObject.getString("gender").contains("female")) {
					userTo.setGender(GENDER.FEMALE);
				} else {
					userTo.setGender(GENDER.MALE);
				}
			}
			if (jsonObject.has("locale")) {
				userTo.setLanguage(jsonObject.getString("locale"));
			}
		} catch (Exception e) {
			logger.error( e.getMessage(), e);
			e.printStackTrace();
		}
		return userTo;
	}

	public static AppUserTO getFacebookUserProfile(String accessToken) {
		AppUserTO userTo = new AppUserTO();
		String url = "https://graph.facebook.com/me?fields=name,picture,email,first_name,last_name,locale,timezone,gender";
		List<Header> headers = new ArrayList<>();
		headers.add(new BasicHeader("Authorization", "Bearer " + accessToken));
		JSONObject jsonObject = makeJsonGetRequest(url, headers);
		if (null == jsonObject) {
			throw new AppException("Could not fetch user's facebook profile");
		}

		try {
			if (jsonObject.has("email")) {
				userTo.setEmail(jsonObject.getString("email"));
			}
			if (jsonObject.has("name")) {
				userTo.setDisplayName(jsonObject.getString("name"));
			}
			if (jsonObject.has("first_name")) {
				userTo.setFirstName(jsonObject.getString("first_name"));
			}
			if (jsonObject.has("last_name")) {
				userTo.setLastName(jsonObject.getString("last_name"));
			}
			if (jsonObject.has("picture") && jsonObject.getJSONObject("picture").has("data") && jsonObject.getJSONObject("picture").getJSONObject("data").has("url")) {
				userTo.setPhotoUrl(jsonObject.getJSONObject("picture").getJSONObject("data").getString("url"));
			}

			if (jsonObject.has("gender")) {
				if (jsonObject.getString("gender").contains("female")) {
					userTo.setGender(GENDER.FEMALE);
					;
				} else {
					userTo.setGender(GENDER.MALE);
				}
			}
			if (jsonObject.has("locale")) {
				userTo.setLanguage(jsonObject.getString("locale"));
			}
		} catch (Exception e) {
			logger.error( e.getMessage(), e);
			e.printStackTrace();
		}
		return userTo;
	}

}
