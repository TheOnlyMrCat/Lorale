package com.dockdev.lorale.exeption;

public class LoraleException extends RuntimeException {

	public LoraleException() {
	}

	public LoraleException(String message) {
		super(message);
	}

	public LoraleException(String message, Throwable cause) {
		super(message, cause);
	}

	public LoraleException(Throwable cause) {
		super(cause);
	}

	public LoraleException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
