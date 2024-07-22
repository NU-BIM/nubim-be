package com.soyeon.nubim.domain.post.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PostNotFoundException extends ResponseStatusException {
	public PostNotFoundException(Long albumId) {
		super(HttpStatus.NOT_FOUND, "Album not found with id " + albumId);
	}

}
