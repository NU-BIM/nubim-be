package com.soyeon.nubim.domain.album.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidContentTypeException extends ResponseStatusException {
	public InvalidContentTypeException(String message) {
		super(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Invalid content type: " + message);
	}
}
