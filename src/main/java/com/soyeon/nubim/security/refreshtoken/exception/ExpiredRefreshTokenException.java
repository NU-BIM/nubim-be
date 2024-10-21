package com.soyeon.nubim.security.refreshtoken.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ExpiredRefreshTokenException extends ResponseStatusException {
	public ExpiredRefreshTokenException(String refreshToken) {
		super(HttpStatus.UNAUTHORIZED, "Refresh token has expired: " + refreshToken);
	}
}