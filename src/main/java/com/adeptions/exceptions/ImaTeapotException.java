package com.adeptions.exceptions;

public class ImaTeapotException extends NasapiException {
	@Override
	public int getStatusCode() {
		return 418;
	}

	public ImaTeapotException(String message) {
		super(message);
	}
}
