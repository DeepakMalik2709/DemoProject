package com.notes.nicefact.service;

import java.io.IOException;

import com.google.api.client.extensions.java6.auth.oauth2.AbstractPromptReceiver;
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver;

public class GoogleReceiverService extends AbstractPromptReceiver implements VerificationCodeReceiver {

	
	@Override
	  public String getRedirectUri() throws IOException {
	    return "http://localhost:8080/a/oauth/googleCallback";
	  }
	
}
