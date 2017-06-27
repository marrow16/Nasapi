package com.adeptions.exceptions;

public class BadRequestException extends NasapiException {
	@Override
	public int getStatusCode() {
		return 400;
	}

	public BadRequestException(String message) {
		super(message);
	}
}
