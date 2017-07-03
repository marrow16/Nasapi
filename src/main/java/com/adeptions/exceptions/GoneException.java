package com.adeptions.exceptions;

public class GoneException extends NasapiException {
	@Override
	public int getStatusCode() {
		return 410;
	}

	public GoneException(String message) {
		super(message);
	}
}
