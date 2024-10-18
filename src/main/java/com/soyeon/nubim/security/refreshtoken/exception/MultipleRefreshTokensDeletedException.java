package com.soyeon.nubim.security.refreshtoken.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class MultipleRefreshTokensDeletedException extends ResponseStatusException {
	public MultipleRefreshTokensDeletedException(int count, String email) {
		super(HttpStatus.INTERNAL_SERVER_ERROR,
			String.format("Multiple Refresh Tokens (%d) were deleted for email: %s", count, email));
	}
}
