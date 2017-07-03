package com.adeptions.exceptions;

public class MiscException extends NasapiException {
	private int statusCode;

	@Override
	public int getStatusCode() {
		return statusCode;
	}

	public MiscException(int statusCode, String message) {
		super(message);
		this.statusCode = statusCode;
	}

	public MiscException(int statusCode, String message, Throwable cause) {
		super(message, cause);
		this.statusCode = statusCode;
	}
}
