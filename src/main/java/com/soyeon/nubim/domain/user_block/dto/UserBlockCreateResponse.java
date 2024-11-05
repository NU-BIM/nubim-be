package com.soyeon.nubim.domain.user_block.dto;

import java.time.LocalDateTime;

import com.soyeon.nubim.domain.user.dto.UserSimpleResponseDto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserBlockCreateResponse {
	private UserSimpleResponseDto blockedUser;
	private LocalDateTime blockedAt;
}
