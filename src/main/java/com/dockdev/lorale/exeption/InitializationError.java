package com.dockdev.lorale.exeption;

public class InitializationError extends LoraleException {
	public InitializationError() {
	}

	public InitializationError(String message) {
		super(message);
	}

	public InitializationError(String message, Throwable cause) {
		super(message, cause);
	}

	public InitializationError(Throwable cause) {
		super(cause);
	}

	public InitializationError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
