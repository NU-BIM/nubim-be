package com.soyeon.nubim.security.refreshtoken.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidRefreshTokenException extends ResponseStatusException {
	public InvalidRefreshTokenException(String message) {
		super(HttpStatus.BAD_REQUEST, "Invalid Refresh token : " + message);
	}
}