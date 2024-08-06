package com.soyeon.nubim.domain.user.exception;

public class InvalidNicknameFormatException extends RuntimeException {
	public InvalidNicknameFormatException(String reason) {
		super("Invalid nickname format: " + reason);
	}
}
