package com.notes.nicefact.exception;

public class UnauthorizedException extends AppException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UnauthorizedException(){
		super(401, "User not authorized to perform this operation.");
	}
	public UnauthorizedException( String arg0) {
		super(401, arg0);
	}

}
