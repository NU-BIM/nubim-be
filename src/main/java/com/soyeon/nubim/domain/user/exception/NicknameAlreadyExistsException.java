package com.soyeon.nubim.domain.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NicknameAlreadyExistsException extends ResponseStatusException {
	public NicknameAlreadyExistsException(String nickname) {
		super(HttpStatus.CONFLICT, "Nickname already exists: " + nickname);
	}
}
