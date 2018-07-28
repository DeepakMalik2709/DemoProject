package com.notes.nicefact.service;

import java.util.Random;

import javax.persistence.EntityManager;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.exception.FirebaseChannelNotFoundException;
import com.notes.nicefact.to.NotificationTO;
import com.notes.nicefact.util.AppProperties;
import com.notes.nicefact.util.CacheUtils;
import com.notes.nicefact.util.Constants;
import com.notes.nicefact.util.EntityManagerHelper;
import com.notes.nicefact.util.Utils;

public class FirebaseService {
	private static final Logger logger = Logger.getLogger(FirebaseService.class.getName());
	private static String SERVER_KEY = "AAAAEQTTmfE:APA91bEsm84CfIcah2ej1EKTv6Vqhzo0e9C3WwflZ8bxbybODwDfheu6n57e-mLGBhMsefZ7OdcSCRSP1_ZTxw6U3bl1pGcSe6BXNHSDgCM0K4IjHOwIqOjOvhgXw-6E_e2SQhyLjfiK73Ma8vc3X-k_ONRALVo2yw";
	private static final String MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";

	private static FirebaseService instance;

	public static FirebaseService getInstance() {
		if (instance == null) {
			instance = new FirebaseService();
		}
		return instance;
	}

	private FirebaseService() {
	}

	public void sendMessage(String email, NotificationTO notificationTO) {
		AppUser user = CacheUtils.getAppUser(email);
		if (null == user) {
			logger.warn("user is null for email : " + email);
		} else {
			String url = Utils.getUrlFromNotification(notificationTO);
			if (StringUtils.isBlank(url)) {
				url = AppProperties.getInstance().getApplicationUrl();
			}
			sendMessage(user, notificationTO.getTitle(), url, notificationTO.getComment());
		}
	}

	public void sendMessage(String email, String title, String url, String body) {
		AppUser user = CacheUtils.getAppUser(email);
		if (null == user) {
			logger.warn("user is null for email : " + email);
		} else {
			sendMessage(user, title, url, body);
		}
	}

	public void sendMessage(AppUser user, String title, String url, String body) {
		if (!(user == null || user.getFirebaseChannelKeys().isEmpty() || StringUtils.isBlank(title))) {
			try {
				JSONObject json = new JSONObject();
				json.put("title", title);
				if (StringUtils.isNotBlank(url)) {
					if (!url.startsWith("http")) {
						if (!url.startsWith("/")) {
							url = "/" + url;
						}
						url = AppProperties.getInstance().getApplicationUrl() + url;
					}
					json.put("click_action", url);
				}
				if (StringUtils.isNotBlank(body)) {
					json.put("body", body);
				}
				for (String channelKey : user.getFirebaseChannelKeys()) {
					try {
						sendFirebaseMessage(channelKey, json);
					} catch (FirebaseChannelNotFoundException e) {
						logger.error(e.getMessage() + " , email : " + user.getEmail() + " , channel : " + channelKey);
						removeChannelKeyForAppuser(user.getEmail() , channelKey);
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		} else {
			logger.error("user : " + user + " , title : " + title + " , both are required.");
		}
	}

	private void removeChannelKeyForAppuser(String email, String channelKey) {
		try {
			logger.info("removeChannelKeyForAppuser email : " + email + " , channelKey : " + channelKey );
			EntityManager em = EntityManagerHelper.getDefaulteEntityManager();
			AppUserService appUserService = new AppUserService(em);
			AppUser user = appUserService.getAppUserByEmail(email);
			user.getFirebaseChannelKeys().remove(channelKey);
			appUserService.upsert(user);
			em.close();
			logger.info("removeChannelKeyForAppuser success");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}

	private void sendFirebaseMessage(String channelKey, JSONObject notification) throws Exception {
		if (channelKey != null) {
			HttpClient client = HttpClients.createDefault();
			for (int i = 0; i < 4; i++) {
				logger.info("i: " + i);
				try {channelKey = channelKey + "1";
					JSONObject parentJson = new JSONObject();
					parentJson.put("notification", notification);
					parentJson.put("to", channelKey);
					String completeMessage = parentJson.toString();
					HttpPost req = new HttpPost(MESSAGE_URL);
					req.addHeader("Authorization", "Key=" + SERVER_KEY);
					req.addHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_JSON);
					ByteArrayEntity entity = new ByteArrayEntity(completeMessage.getBytes(Constants.UTF_8));
					req.setEntity(entity);
					HttpResponse resp = client.execute(req);
					String respMessage = null;
					if (null !=resp && null != resp.getEntity()) {
						respMessage = new String(IOUtils.toByteArray(resp.getEntity().getContent()), Constants.UTF_8);
					}
					if (resp.getStatusLine().getStatusCode() == 200) {
						if(respMessage.contains("\"failure\":1")){
							throw new FirebaseChannelNotFoundException();
						}
						break;
					} else {
						logger.error(resp.getStatusLine().getStatusCode() + " : " + respMessage);
						Thread.sleep((i * 1000) + new Random().nextInt(1000));
					}
				} catch (FirebaseChannelNotFoundException e) {
					throw e;
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
				/* if we reach here there was error sending message */
				
			}

		}
	}

}
