package com.notes.nicefact.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.notes.nicefact.util.AppProperties;
import com.notes.nicefact.util.Constants;
import com.notes.nicefact.util.Utils;

/**
 * https://www.ably.io/documentation/rest-api
 * decided to use ably as it is cheaper and works on ios safari
 * 
 */
public class PushService {
	public static final Logger logger = Logger.getLogger(PushService.class.getName());
	public static final String ENDPOINT = "https://rest.ably.io/";

	private PushService() {
	}

	public static PushService instance;

	public static PushService getInstance() {
		if (instance == null) {
			instance = new PushService();
		}
		return instance;
	}
	
	/*public static void main(String[] args) throws Exception {
		JSONObject metadata = new JSONObject();
		metadata.put("name", "greeting");
		metadata.put("data", "hola");
		sendChannelMessage("jitender@nicefact.co.in", metadata);
		
	}*/

	public void sendChannelMessage(String channelId, Object msg) throws Exception {
		logger.info("start sendChannelMessage");
		try {
			PushService ps = PushService.getInstance();
			String url = ENDPOINT + "/channels/" + channelId + "/messages";
			String data = Utils.jsonify(msg);
			List<Header> headers = new ArrayList<>();
			headers.add(new BasicHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_JSON));
			JSONObject resp = ps.doJsonPost(url, headers, data.getBytes(Constants.UTF_8));
			if (resp == null) {
				logger.error("sendChannelMessage failed");
			} else {
				logger.info(resp);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		logger.info("exit sendChannelMessage");
	}

	public Map<String, Object> getToken() throws Exception {
		logger.info("start getToken");
		try {
			Map<String, Object> tokenJson = new HashMap<>();
			PushService ps = PushService.getInstance();
			AppProperties appProperties = AppProperties.getInstance();
			String url = ENDPOINT + "keys/" + appProperties.getAblyAppId() + "/requestToken";
			JSONObject metadata = new JSONObject();
			metadata.put("keyName", appProperties.getAblyAppId());
			metadata.put("timestamp", new Date().getTime());
			List<Header> headers = new ArrayList<>();
			headers.add(new BasicHeader(Constants.CONTENT_TYPE, Constants.CONTENT_TYPE_JSON));
			JSONObject resp = ps.doJsonPost(url, headers, metadata.toString().getBytes(Constants.UTF_8));
			if (null != resp) {
				tokenJson.put("token", resp.getString("token"));
				tokenJson.put("expires", resp.getLong("expires"));
				return tokenJson;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		logger.info("exit getToken");
		return null;
	}

	public JSONObject doJsonPost(String url, List<Header> headers, byte[] payload) {
		HttpResponse resp = doPost(url, headers, payload);
		if (null == resp) {
			logger.error(url + " , null response");
		} else {
			return Utils.getJsonFromResponse(resp);
		}
		return null;
	}

	public HttpResponse doPost(String url, List<Header> headers, byte[] payload) {
		logger.info("doPost start , url : " + url);
		HttpClient client = HttpClients.createDefault();
		for (int i = 0; i < 3; i++) {
			try {
				HttpPost request1 = new HttpPost(url);
				logger.info("attempt : " + i);
				String headerKey = AppProperties.getInstance().getAblyAppId() + ":" + AppProperties.getInstance().getAblyKey();
				String encodedHeaderKey = Base64.encodeBase64URLSafeString(headerKey.getBytes(Constants.UTF_8));
				request1.addHeader("Authorization", "Basic " + encodedHeaderKey);
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
					// Utils.refreshToken(user);

				} else if (response.getEntity() != null) {
					String respStr = new String(IOUtils.toByteArray(response.getEntity().getContent()), Constants.UTF_8);
					logger.info(respStr);
					if (respStr.contains("invalidSharingRequest")) {
						return null;
					}
				}
				request1.releaseConnection();
				Thread.sleep((i * 1000) + new Random().nextInt(1000));
			} catch (Exception e) {
				if (i == 2) {
					logger.error(" failed request , " + e.getMessage(), e);
				}
			}
		}
		logger.info("doPost exit");
		return null;
	}
}
