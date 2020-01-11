package com.fbi.engine.plugins.core.sql;

public class DriverLoadingException extends RuntimeException {

	private static final long serialVersionUID = 4557152246635783849L;

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
