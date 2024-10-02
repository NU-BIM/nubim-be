package com.soyeon.nubim.domain.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class EmailAlreadyExistsException extends ResponseStatusException {
	public EmailAlreadyExistsException(String email) {
		super(HttpStatus.CONFLICT, "Email already exists: " + email);
	}
}