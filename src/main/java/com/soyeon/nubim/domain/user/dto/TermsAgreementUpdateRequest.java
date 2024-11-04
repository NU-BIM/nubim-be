package com.soyeon.nubim.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TermsAgreementUpdateRequest {
	private boolean privacyAgreement;
	private boolean serviceAgreement;
}
