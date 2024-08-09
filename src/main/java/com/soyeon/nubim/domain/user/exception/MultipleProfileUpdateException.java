package com.soyeon.nubim.domain.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class MultipleProfileUpdateException extends ResponseStatusException {
	public MultipleProfileUpdateException() {
		super(HttpStatus.INTERNAL_SERVER_ERROR,
			"Multiple profiles were unexpectedly updated. Only one profile should be modified at a time.");
	}
}
