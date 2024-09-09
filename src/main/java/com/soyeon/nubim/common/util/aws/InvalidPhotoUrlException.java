package com.soyeon.nubim.common.util.aws;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidPhotoUrlException extends ResponseStatusException {
	public InvalidPhotoUrlException() {
		super(HttpStatus.BAD_REQUEST, "Invalid Photo URL");
	}
}
