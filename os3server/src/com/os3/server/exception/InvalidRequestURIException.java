package com.os3.server.exception;

public class InvalidRequestURIException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4133506492985738918L;

	/**
	 * Instantiates a new bad request exception.
	 *
	 * @param arg0 the arg0
	 */
	public InvalidRequestURIException(String arg0) {
		super(arg0);
	}
}
