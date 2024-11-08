package com.soyeon.nubim.domain.userfollow.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class IsFollowingResponse {
	private Long followerId;
	private Long followeeId;

	private Boolean isFollowing;
}
