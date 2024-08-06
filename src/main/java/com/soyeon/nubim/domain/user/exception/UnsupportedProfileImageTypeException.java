package com.soyeon.nubim.domain.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
public class UnsupportedProfileImageTypeException extends RuntimeException {
	public UnsupportedProfileImageTypeException(String contentType) {
		super("Unsupported profile image type : " + contentType);
	}
}
