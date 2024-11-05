package com.soyeon.nubim.domain.user_block.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class MultipleUserBlockDeletedException extends ResponseStatusException {
	public MultipleUserBlockDeletedException() {
		super(HttpStatus.INTERNAL_SERVER_ERROR, "Multiple UserBlock deleted");
	}
}
