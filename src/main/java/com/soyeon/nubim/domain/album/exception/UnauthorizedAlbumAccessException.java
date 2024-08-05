package com.soyeon.nubim.domain.album.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UnauthorizedAlbumAccessException extends ResponseStatusException {
	public UnauthorizedAlbumAccessException(Long albumId) {
		super(HttpStatus.FORBIDDEN, "You are not authorized to access this Album id: " + albumId);
	}
}
