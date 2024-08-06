package com.soyeon.nubim.common.exception_handler;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidQueryParameterException extends ResponseStatusException {
	public InvalidQueryParameterException(String parameterName) {
		super(HttpStatus.BAD_REQUEST, "Invalid query parameter: " + parameterName);
	}
}
