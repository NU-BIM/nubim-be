package com.soyeon.nubim.domain.album.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AlbumAlreadyLinkedToPostException extends ResponseStatusException {
	public AlbumAlreadyLinkedToPostException() {
		super(HttpStatus.BAD_REQUEST, "This album is already linked to a post.");
	}
}
