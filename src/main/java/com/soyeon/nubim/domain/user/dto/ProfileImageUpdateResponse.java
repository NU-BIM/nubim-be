package com.soyeon.nubim.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProfileImageUpdateResponse {
	private String message;
	private String profileImageUrl;
}
