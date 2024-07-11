package com.soyeon.nubim.domain.album;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AlbumNotFoundException extends ResponseStatusException {
	public AlbumNotFoundException(Long albumId) {
		super(HttpStatus.NOT_FOUND, "Album not found with id " + albumId);
	}
}
