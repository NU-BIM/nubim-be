package com.soyeon.nubim.domain.post.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PostNotFoundException extends ResponseStatusException {
	public PostNotFoundException(Long postId) {
		super(HttpStatus.NOT_FOUND, "Post not found with id " + postId);
	}
}
