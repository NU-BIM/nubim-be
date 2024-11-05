package com.soyeon.nubim.domain.comment.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CommentNotFoundException extends ResponseStatusException {
	public CommentNotFoundException(Long commentId) {
		super(HttpStatus.NOT_FOUND, "Comment not found with id " + commentId);
	}
}
