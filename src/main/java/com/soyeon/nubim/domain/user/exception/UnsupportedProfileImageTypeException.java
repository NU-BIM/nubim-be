package com.soyeon.nubim.domain.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UnsupportedProfileImageTypeException extends ResponseStatusException {
	public UnsupportedProfileImageTypeException(String contentType) {
		super(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported profile image type : " + contentType);
	}
}
