package com.adeptions.exceptions;

public class NotAcceptableException extends NasapiException {
	@Override
	public int getStatusCode() {
		return 406;
	}

	public NotAcceptableException(String message) {
		super(message);
	}
}
