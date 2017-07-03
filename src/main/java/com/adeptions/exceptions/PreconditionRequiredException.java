package com.adeptions.exceptions;

public class PreconditionRequiredException extends NasapiException {
	@Override
	public int getStatusCode() {
		return 428;
	}

	public PreconditionRequiredException(String message) {
		super(message);
	}
}
