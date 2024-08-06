package com.soyeon.nubim.domain.user.exception;

public class MultipleProfileUpdateException extends RuntimeException {
	public MultipleProfileUpdateException() {
		super("Multiple profiles were unexpectedly updated. Only one profile should be modified at a time.");
	}
}
