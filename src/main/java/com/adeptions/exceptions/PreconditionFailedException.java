package com.adeptions.exceptions;

public class PreconditionFailedException extends NasapiException {
	@Override
	public int getStatusCode() {
		return 412;
	}

	public PreconditionFailedException(String message) {
		super(message);
	}
}
