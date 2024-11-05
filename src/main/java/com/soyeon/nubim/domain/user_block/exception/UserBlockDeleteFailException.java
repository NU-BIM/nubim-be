package com.soyeon.nubim.domain.user_block.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserBlockDeleteFailException extends ResponseStatusException {
	public UserBlockDeleteFailException() {
		super(HttpStatus.BAD_REQUEST, "UserBlock does not exist");
	}
}