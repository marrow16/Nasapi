package com.adeptions.exceptions;

public class NasapiException extends Exception {
	public int getStatusCode() {
		return 500;
	}

	public NasapiException(String message) {
		super(message);
	}

	public NasapiException(String message, Throwable cause) {
		super(message, cause);
	}
}
