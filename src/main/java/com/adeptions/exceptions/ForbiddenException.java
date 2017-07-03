package com.adeptions.exceptions;

public class ForbiddenException extends NasapiException {
	@Override
	public int getStatusCode() {
		return 403;
	}

	public ForbiddenException(String message) {
		super(message);
	}
}
