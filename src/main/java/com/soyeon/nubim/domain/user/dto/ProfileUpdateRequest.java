package com.soyeon.nubim.domain.user.dto;

import java.time.LocalDateTime;

import com.soyeon.nubim.common.enums.Gender;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileUpdateRequest {
	private String username;
	private String nickname;
	private String profileIntroduction;
	private String phoneNumber;
	private LocalDateTime birthDate;
	@Enumerated(EnumType.STRING)
	private Gender gender;
}
