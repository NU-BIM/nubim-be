package com.soyeon.nubim.domain.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class DeletedUserSignupAttemptException extends ResponseStatusException {
	public DeletedUserSignupAttemptException(String email) {
		super(HttpStatus.FORBIDDEN, "deleted account : " + email);
	}
}