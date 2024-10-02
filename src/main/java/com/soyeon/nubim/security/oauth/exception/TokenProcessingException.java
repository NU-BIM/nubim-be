package com.soyeon.nubim.security.oauth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class TokenProcessingException extends ResponseStatusException {
	public TokenProcessingException(String message) {
		super(HttpStatus.INTERNAL_SERVER_ERROR, "Token processing error: " + message);
	}
}