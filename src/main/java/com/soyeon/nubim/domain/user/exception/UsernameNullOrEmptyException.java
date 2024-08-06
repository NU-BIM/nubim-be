package com.soyeon.nubim.domain.user.exception;

public class UsernameNullOrEmptyException extends RuntimeException {
	public UsernameNullOrEmptyException() {
		super("Username cannot be null or empty");
	}
}
