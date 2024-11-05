package com.soyeon.nubim.domain.user_block.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class BlockedUserAccessDeniedException extends ResponseStatusException {
	public BlockedUserAccessDeniedException() {
		super(HttpStatus.FORBIDDEN, "blocked user access denied");
	}
}