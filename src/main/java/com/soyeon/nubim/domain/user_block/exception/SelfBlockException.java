package com.soyeon.nubim.domain.user_block.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class SelfBlockException extends ResponseStatusException {
	public SelfBlockException() {
		super(HttpStatus.BAD_REQUEST, "cannot block yourself");
	}
}