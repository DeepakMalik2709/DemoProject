package com.notes.nicefact.google;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.calendar.Calendar;
import com.notes.nicefact.controller.CalendarController;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.util.AppProperties;
import com.notes.nicefact.util.CurrentContext;

public class GoogleAppUtils {
	private final static Logger logger = Logger.getLogger(GoogleAppUtils.class.getName());
	private static final JsonFactory JSON_FACTORY =   JacksonFactory.getDefaultInstance();

    private static HttpTransport HTTP_TRANSPORT;
 
	private static  GoogleCredential credential;
	 
	private static Calendar calendarService;

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
          
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

  
    public static Credential getCredential() throws IOException {
    	if(credential==null){
    		 AppUser user = CurrentContext.getAppUser();
    		AppProperties	appProperties=	AppProperties.getInstance();
    		 credential = new GoogleCredential.Builder()
	            .setTransport(HTTP_TRANSPORT)
	            .setJsonFactory(JSON_FACTORY)
	            .setClientSecrets(appProperties.getGoogleClientId(),appProperties.getGoogleClientSecret() ).build();
    		 if(user!=null && credential.getAccessToken()==null){
    		
    		 credential.setAccessToken(user.getAccessToken());
    		 }
    	}
	    
	     return credential;
    }    
    
    
    public static com.google.api.services.calendar.Calendar    getCalendarService() throws IOException {
    	AppUser user = CurrentContext.getAppUser();
    	if(user.getUseGoogleCalendar()){  	
    		if(calendarService==null){
    			calendarService = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredential())
                .setApplicationName(AppProperties.getInstance().getAppName())
                .build();
    		}
    		return calendarService;
    	}
		return null;
    }
    
    public static Credential getCredential(AppUser user) throws IOException {
    	if(credential==null){
    		AppProperties	appProperties=	AppProperties.getInstance();
    		 credential = new GoogleCredential.Builder()
	            .setTransport(HTTP_TRANSPORT)
	            .setJsonFactory(JSON_FACTORY)
	            .setClientSecrets(appProperties.getGoogleClientId(),appProperties.getGoogleClientSecret() ).build();
    		 if(user!=null && credential.getAccessToken()==null){
    		
    		 credential.setAccessToken(user.getAccessToken());
    		 }
    	}
	    
	     return credential;
    }    
    
    public static com.google.api.services.calendar.Calendar    getCalendarService(AppUser user) throws IOException {
    	if(user.getUseGoogleCalendar()){  	
    		if(calendarService==null){
    			calendarService = new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredential(user))
                .setApplicationName(AppProperties.getInstance().getAppName())
                .build();
    		}
    		return calendarService;
    	}
		return null;
    }
}
