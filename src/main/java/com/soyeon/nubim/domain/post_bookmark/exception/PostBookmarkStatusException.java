package com.soyeon.nubim.domain.post_bookmark.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PostBookmarkStatusException extends ResponseStatusException {
	public PostBookmarkStatusException(String message) {
		super(HttpStatus.BAD_REQUEST, message);
	}

	public static PostBookmarkStatusException alreadyBookmarked(Long postId) {
		return new PostBookmarkStatusException("You have already bookmarked the postId: " + postId);
	}

	public static PostBookmarkStatusException notBookmarked(Long postId) {
		return new PostBookmarkStatusException("You have not bookmarked the postId: " + postId);
	}
}
