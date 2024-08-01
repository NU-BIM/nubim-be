package com.soyeon.nubim.domain.user.dto;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode(of = {"nickname"})
@Getter
@Builder
public class UserFollowResponseDto {
	private String username;
	private String nickname;
	private String profileImageUrl;
	private boolean isFollowing;

	public void setIsFollowingTrue() {
		this.isFollowing = true;
	}
}
