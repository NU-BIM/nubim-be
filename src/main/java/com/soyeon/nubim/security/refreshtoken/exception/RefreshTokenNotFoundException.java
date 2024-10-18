package com.soyeon.nubim.security.refreshtoken.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class RefreshTokenNotFoundException extends ResponseStatusException {
	public RefreshTokenNotFoundException(String email) {
		super(HttpStatus.NOT_FOUND, "Refresh Token not found: " + email);
	}
}