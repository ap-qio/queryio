package com.queryio.core.requestprocessor;

/**
 * This class implements Match function.
 * 
 * @author Exceed Consultancy Services.
 * @version 1.0
 */
public class TagParserException extends Exception {
	private static final long serialVersionUID = 7526000057010002439L;

	public TagParserException(final String s) {
		super(s);
	}

	public TagParserException(final Exception e) {
		super(e);
	}

	public TagParserException() {
		super();
	}
}