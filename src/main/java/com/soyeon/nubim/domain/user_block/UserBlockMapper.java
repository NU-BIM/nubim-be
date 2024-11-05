package com.soyeon.nubim.domain.user_block;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.soyeon.nubim.domain.user.User;
import com.soyeon.nubim.domain.user.UserMapper;
import com.soyeon.nubim.domain.user.dto.UserSimpleResponseDto;
import com.soyeon.nubim.domain.user_block.dto.UserBlockCreateResponse;
import com.soyeon.nubim.domain.user_block.dto.UserBlockReadResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserBlockMapper {

	private final UserMapper userMapper;

	public UserBlock toEntity(User blockingUser, User blockedUser) {
		return UserBlock.builder()
			.blockingUser(blockingUser)
			.blockedUser(blockedUser)
			.build();
	}

	public UserBlockCreateResponse toUserBlockCreateResponse(UserBlock userBlock) {
		UserSimpleResponseDto blockedUser = userMapper.toUserSimpleResponseDto(userBlock.getBlockedUser());

		return UserBlockCreateResponse.builder()
			.blockedUser(blockedUser)
			.blockedAt(userBlock.getBlockedAt())
			.build();
	}

	public UserBlockReadResponse toUserBlockReadResponse(UserBlock userBlock) {
		UserSimpleResponseDto blockedUser = userMapper.toUserSimpleResponseDto(userBlock.getBlockedUser());

		return UserBlockReadResponse.builder()
			.blockedUser(blockedUser)
			.blockedAt(userBlock.getBlockedAt())
			.build();
	}

	public List<UserBlockReadResponse> toUserBlockReadResponses(List<UserBlock> userBlocks) {
		List<UserBlockReadResponse> userBlockReadResponses = new ArrayList<>(userBlocks.size());
		for (UserBlock userBlock : userBlocks) {
			userBlockReadResponses.add(toUserBlockReadResponse(userBlock));
		}
		return userBlockReadResponses;
	}
}
