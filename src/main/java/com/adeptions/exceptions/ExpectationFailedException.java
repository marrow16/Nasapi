package com.adeptions.exceptions;

public class ExpectationFailedException extends NasapiException {
	@Override
	public int getStatusCode() {
		return 417;
	}

	public ExpectationFailedException(String message) {
		super(message);
	}
}
