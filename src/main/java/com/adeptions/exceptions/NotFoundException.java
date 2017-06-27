package com.adeptions.exceptions;

public class NotFoundException extends NasapiException {
	@Override
	public int getStatusCode() {
		return 404;
	}

	public NotFoundException(String message) {
		super(message);
	}
}
