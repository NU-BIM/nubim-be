package com.soyeon.nubim.domain.user.exception;

public class NicknameNullOrEmptyException extends RuntimeException {
	public NicknameNullOrEmptyException() {
		super("Nickname cannot be null or empty");
	}
}
