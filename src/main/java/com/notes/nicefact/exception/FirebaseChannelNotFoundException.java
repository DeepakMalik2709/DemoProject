package com.notes.nicefact.exception;

public class FirebaseChannelNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FirebaseChannelNotFoundException() {
		super("Firebase channel not found. message sending failed.");
	}

}
