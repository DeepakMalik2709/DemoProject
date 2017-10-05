package com.notes.nicefact.exception;

public class ServiceException extends AppException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ServiceException( String arg0) {
		super(403, arg0);
	}

}
