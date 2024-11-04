package com.soyeon.nubim.domain.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserAgreementUpdateFailException extends ResponseStatusException {
	public UserAgreementUpdateFailException() {
		super(HttpStatus.INTERNAL_SERVER_ERROR, "terms agreement update fail");
	}
}
