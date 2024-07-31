package com.soyeon.nubim.domain.user.dto;

import java.util.Objects;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserFollowResponseDto {
	private String username;
	private String nickname;
	private String profileImageUrl;
	private boolean isFollowing;

	public void setIsFollowingTrue(){
		this.isFollowing = true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		UserFollowResponseDto that = (UserFollowResponseDto)o;
		return Objects.equals(nickname, that.nickname);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(nickname);
	}
}
