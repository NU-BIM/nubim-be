package com.soyeon.nubim.domain.user;

import org.springframework.stereotype.Component;

import com.soyeon.nubim.domain.user.dto.UserSimpleResponseDto;

@Component
public class UserMapper {
	public UserSimpleResponseDto toUserSimpleResponseDto(User user) {
		return UserSimpleResponseDto.builder()
			.userId(user.getUserId())
			.nickname(user.getNickname())
			.profileImageUrl(user.getProfileImageUrl())
			.build();
	}
}
