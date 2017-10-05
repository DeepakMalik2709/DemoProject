package com.notes.nicefact.google;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets.Details;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.notes.nicefact.controller.OauthController;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.util.AppProperties;
import com.notes.nicefact.util.Constants;


public class GoogleAppUtils {

	GoogleClientSecrets clientSecrets ;
	public static final String GOOGLE_CALLBACK = "googleCallback";
	
	   private static final JsonFactory JSON_FACTORY =
		        JacksonFactory.getDefaultInstance();
	   
	public static GoogleClientSecrets getClientSecrets() {

		GoogleClientSecrets clientSecrets = new GoogleClientSecrets();
        Details web=new Details();
        web.setClientId(AppProperties.getInstance().getGoogleClientId());
        web.setClientSecret(AppProperties.getInstance().getGoogleClientSecret());
        web.setFactory(JSON_FACTORY);
        List<String> redirectUris = new ArrayList<String>();
        redirectUris.add(AppProperties.getInstance().getApplicationUrl() + "/a/oauth/" + GOOGLE_CALLBACK);
        web.setRedirectUris(redirectUris);
        web.setTokenUri(OauthController.GOOGLE_OAUTH_TOKEN_URL);
		clientSecrets.setWeb(web);
		return clientSecrets;
	}
	
	

	
}
