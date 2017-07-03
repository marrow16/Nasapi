package com.adeptions.exceptions;

public class UnauthotizedException extends NasapiException {
	@Override
	public int getStatusCode() {
		return 401;
	}

	public UnauthotizedException(String message) {
		super(message);
	}
}
