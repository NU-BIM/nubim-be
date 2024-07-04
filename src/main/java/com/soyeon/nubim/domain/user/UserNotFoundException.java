package com.soyeon.nubim.domain.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserNotFoundException extends ResponseStatusException {
	public UserNotFoundException(Long userId) {
		super(HttpStatus.NOT_FOUND, "User not found with id " + userId);
	}
}
