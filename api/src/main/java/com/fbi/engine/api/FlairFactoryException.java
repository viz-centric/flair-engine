package com.fbi.engine.api;

/**
 * Exception occurring on flair factory operations.
 */
public class FlairFactoryException extends RuntimeException {

	private static final long serialVersionUID = 801517096451659943L;

	public FlairFactoryException() {
		super();
	}

	public FlairFactoryException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public FlairFactoryException(String message, Throwable cause) {
		super(message, cause);
	}

	public FlairFactoryException(String message) {
		super(message);
	}

	public FlairFactoryException(Throwable cause) {
		super(cause);
	}

}
