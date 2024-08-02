package com.soyeon.nubim.domain.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserNotFoundException extends ResponseStatusException {
	public UserNotFoundException(Long userId) {
		super(HttpStatus.NOT_FOUND, "User not found with id " + userId);
	}

	public UserNotFoundException(String identifier, String type) {
		super(HttpStatus.NOT_FOUND, "User not found with " + type + " " + identifier);
	}

	public static UserNotFoundException forEmail(String email) {
		return new UserNotFoundException(email, "email");
	}

	public static UserNotFoundException forNickname(String nickname) {
		return new UserNotFoundException(nickname, "nickname");
	}
}
