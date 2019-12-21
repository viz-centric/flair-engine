package com.fbi.engine.api;

/**
 * Exception occurs when driver loading has failed.
 * 
 * @see QueryExecutor
 */
public class DriverLoadingException extends FlairFactoryException {

	private static final long serialVersionUID = -7925106124447143497L;

	public DriverLoadingException() {
		super();
	}

	public DriverLoadingException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DriverLoadingException(String message, Throwable cause) {
		super(message, cause);
	}

	public DriverLoadingException(String message) {
		super(message);
	}

	public DriverLoadingException(Throwable cause) {
		super(cause);
	}

}
