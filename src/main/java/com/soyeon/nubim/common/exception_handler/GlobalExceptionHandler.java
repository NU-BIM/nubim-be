package com.soyeon.nubim.common.exception_handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ErrorResponse> handle(ResponseStatusException ex) {
		ErrorResponse errorResponse = ErrorResponse.valueOf(ex);
		return new ResponseEntity<>(errorResponse, ex.getStatusCode());
	}
	
}
