package com.adeptions.exceptions;

public class MappingException extends NasapiException {
	public MappingException(String message) {
		super(message);
	}

	public MappingException(String message, Throwable cause) {
		super(message, cause);
	}
}
