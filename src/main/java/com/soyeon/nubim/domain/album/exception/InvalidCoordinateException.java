package com.soyeon.nubim.domain.album.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidCoordinateException extends ResponseStatusException {
	public InvalidCoordinateException(int actualDimension) {
		super(HttpStatus.BAD_REQUEST, "Invalid coordinate: Expected 2 values, but got " + actualDimension);
	}
}