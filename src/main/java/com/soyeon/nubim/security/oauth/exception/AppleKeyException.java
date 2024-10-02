package com.soyeon.nubim.security.oauth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AppleKeyException extends ResponseStatusException {
	public AppleKeyException(String message) {
		super(HttpStatus.SERVICE_UNAVAILABLE, "Apple key error: " + message);
	}
}
