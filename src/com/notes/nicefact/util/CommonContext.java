package com.notes.nicefact.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Use this class now onwards for session management
 * 
 * @author jkb
 *
 */
public class CommonContext implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String message;
	
	String passwordResetCode;
	
	String redirectUrl;

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("message" , getMessage());
		return map;
	}


	
	/* getter setters here */
	


	public String getMessage() {
		return message;
	}



	public String getRedirectUrl() {
		return redirectUrl;
	}



	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}



	public void setMessage(String message) {
		this.message = message;
	}

	public String getPasswordResetCode() {
		return passwordResetCode;
	}

	public void setPasswordResetCode(String passwordResetCode) {
		this.passwordResetCode = passwordResetCode;
	}
}
