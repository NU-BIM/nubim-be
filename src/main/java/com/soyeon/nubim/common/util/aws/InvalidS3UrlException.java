package com.soyeon.nubim.common.util.aws;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidS3UrlException extends ResponseStatusException {
	public InvalidS3UrlException() {
		super(HttpStatus.BAD_REQUEST, "Invalid S3 URL format");
	}
}
