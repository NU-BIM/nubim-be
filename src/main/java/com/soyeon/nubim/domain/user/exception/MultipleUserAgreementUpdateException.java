package com.soyeon.nubim.domain.user.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class MultipleUserAgreementUpdateException extends ResponseStatusException {
	public MultipleUserAgreementUpdateException(int updatedCount) {
		super(HttpStatus.INTERNAL_SERVER_ERROR,
			String.format("Multiple users' agreements were unexpectedly updated (%d users). Only one user's agreement should be modified at a time.", updatedCount));
	}
}
