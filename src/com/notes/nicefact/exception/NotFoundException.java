package com.notes.nicefact.exception;

public class NotFoundException extends AppException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NotFoundException( String arg0) {
		super(404, arg0);
	}

}
