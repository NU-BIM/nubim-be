package com.soyeon.nubim.domain.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UsernameNullOrEmptyException extends ResponseStatusException {
	public UsernameNullOrEmptyException() {
		super(HttpStatus.BAD_REQUEST, "Username cannot be null or empty");
	}
}
