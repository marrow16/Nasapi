package com.adeptions.exceptions;

public class MethodNotAllowedException extends NasapiException {
	private String allowedMethods;

	public MethodNotAllowedException(String message, String allowedMethods) {
		super(message);
		this.allowedMethods = allowedMethods;
	}

	public String getAllowedMethods() {
		return allowedMethods;
	}

	@Override
	public int getStatusCode() {
		return 405;
	}
}
