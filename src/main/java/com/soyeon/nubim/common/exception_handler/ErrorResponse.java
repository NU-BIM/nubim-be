package com.soyeon.nubim.common.exception_handler;

import java.time.LocalDateTime;

import org.springframework.web.server.ResponseStatusException;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {
	private final LocalDateTime timestamp;
	private final String message;
	private final String code;

	public static ErrorResponse valueOf(ResponseStatusException ex) {
		return ErrorResponse.builder()
			.timestamp(LocalDateTime.now())
			.message(ex.getReason())
			.code(Integer.toString(ex.getStatusCode().value()))
			.build();
	}
}
