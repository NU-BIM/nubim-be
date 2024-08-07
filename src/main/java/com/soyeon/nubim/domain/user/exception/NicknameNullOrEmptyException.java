package com.soyeon.nubim.domain.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NicknameNullOrEmptyException extends ResponseStatusException {
	public NicknameNullOrEmptyException() {
		super(HttpStatus.BAD_REQUEST, "Nickname cannot be null or empty");
	}
}
