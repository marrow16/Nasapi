package com.adeptions.exceptions;

public class ConflictException extends NasapiException {
	@Override
	public int getStatusCode() {
		return 409;
	}

	public ConflictException(String message) {
		super(message);
	}
}
