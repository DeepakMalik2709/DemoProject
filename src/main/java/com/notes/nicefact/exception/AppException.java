package com.notes.nicefact.exception;

/**
 * A generic Application Exception class.
 * 
 * 
 */
public class AppException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	int code ;
	
	public AppException(String arg0) {
		super(arg0);
	}

	public AppException(int code, String arg0) {
		super(code + " : " + arg0);
		this.code = code;
	}

}
