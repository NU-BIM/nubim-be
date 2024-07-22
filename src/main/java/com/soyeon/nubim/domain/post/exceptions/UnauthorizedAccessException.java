package com.soyeon.nubim.domain.post.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UnauthorizedAccessException extends ResponseStatusException {
	public UnauthorizedAccessException(Long postId) {
		super(HttpStatus.FORBIDDEN, "You are not authorized to this Post id : " + postId);
	}
}
