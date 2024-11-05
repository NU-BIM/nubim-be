package com.soyeon.nubim.domain.user_block.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AlreadyBlockedException extends ResponseStatusException {
	public AlreadyBlockedException() {
		super(HttpStatus.CONFLICT, "Already Blocked User");
	}
}
