package com.soyeon.nubim.domain.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidNicknameFormatException extends ResponseStatusException {
	public InvalidNicknameFormatException(String reason) {
		super(HttpStatus.BAD_REQUEST, "Invalid nickname format: " + reason);
	}
}
