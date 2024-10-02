package com.soyeon.nubim.security.oauth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidAppleIdTokenException extends ResponseStatusException {
	public InvalidAppleIdTokenException(String message) {
		super(HttpStatus.UNAUTHORIZED, "Invalid id token: " + message);
	}
}