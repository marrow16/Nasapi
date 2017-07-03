package com.adeptions.exceptions;

public class RequestedRangeNotSatisfiableException extends NasapiException {
	@Override
	public int getStatusCode() {
		return 416;
	}

	public RequestedRangeNotSatisfiableException(String message) {
		super(message);
	}
}
