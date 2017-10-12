package com.notes.nicefact.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.notes.nicefact.entity.AppUser;
import com.notes.nicefact.google.GoogleAppUtils;
import com.notes.nicefact.util.Constants;
import com.notes.nicefact.util.CurrentContext;

public class GoogleCalendarService {

	
	/** Application name. */
    private static final String APPLICATION_NAME ="AllSchool";

    /** Directory to store user credentials for this application. */
    private static final java.io.File DATA_STORE_DIR = new java.io.File(
        "/", ".credentials/calendar-java-quickstart");

    /** Global instance of the {@link FileDataStoreFactory}. */
    private static FileDataStoreFactory DATA_STORE_FACTORY;

    /** Global instance of the JSON factory. */
    private static final JsonFactory JSON_FACTORY =
        JacksonFactory.getDefaultInstance();

    /** Global instance of the HTTP transport. */
    private static HttpTransport HTTP_TRANSPORT;

	public static final String CALENDAR_CALLBACK = "/getEvent";
	
    /** Global instance of the scopes required by this quickstart.
     *
     * If modifying these scopes, delete your previously saved credentials
     * at ~/.credentials/calendar-java-quickstart
     */
    private static final List<String> SCOPES =
        Arrays.asList(CalendarScopes.CALENDAR_READONLY);

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Creates an authorized Credential object.
     * @param request 
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize(HttpServletRequest request) throws IOException {
        // Load client secrets.
      /*  InputStream in =
            Quickstart.class.getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));*/
    	GoogleClientSecrets clientSecrets = GoogleAppUtils.getClientSecrets();
    	HttpSession session =	request.getSession();
    	AppUser user = (AppUser) session.getAttribute(Constants.SESSION_KEY_lOGIN_USER);
       
        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(DATA_STORE_FACTORY)
                .setAccessType("online")
                .build();
        VerificationCodeReceiver rev = new GoogleReceiverService();
      
     /*   TokenResponse response = new TokenResponse();
        response.setAccessToken(user.getAccessToken());
        response.setExpiresInSeconds(8000L);
        response.setFactory(JSON_FACTORY);
        response.setRefreshToken(user.getRefreshToken());
        response.setScope(CalendarScopes.CALENDAR_READONLY);
         Credential   credential=flow.createAndStoreCredential(response, user.getEmail());*/
        Credential credential = new AuthorizationCodeInstalledApp(
            flow,rev ).authorize("kkuldeepjoshi5@gmail.com");
        System.out.println(
                "Credentials saved to " + DATA_STORE_DIR.getAbsolutePath());
        return credential;
    }
    
    /**
     * Build and return an authorized Calendar client service.
     * @param request 
     * @return an authorized Calendar client service
     * @throws IOException
     */
    public static com.google.api.services.calendar.Calendar    getCalendarService() throws IOException {
     //   Credential credential = authorize(request);
    	AppUser user = CurrentContext.getAppUser();
    	if(user.getUseGoogleCalendar()){
    	List<Header> headers = new ArrayList<>();
		headers.add(new BasicHeader("Authorization", "Bearer " + user.getAccessToken()));
		/*String url = "https://www.googleapis.com/calendar/v3/calendars/primary/events";
		JSONObject jsonObject = OauthController.makeJsonGetRequest(url, headers);*/
		
        
    	  GoogleCredential credential = new GoogleCredential().setAccessToken(user.getAccessToken());
        return new com.google.api.services.calendar.Calendar.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    	}
    	/*HttpRequestInitializer httpRequestInitializer;
		return new Calendar(HTTP_TRANSPORT,JSON_FACTORY,httpRequestInitializer);*/
		return null;
    }
}
