package com.soyeon.nubim.domain.postlike.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class MultiplePostLikeDeleteException extends ResponseStatusException {
	public MultiplePostLikeDeleteException() {
		super(HttpStatus.INTERNAL_SERVER_ERROR,
			"Multiple post like were unexpectedly deleted. Only one post like can be deleted");
	}
}
