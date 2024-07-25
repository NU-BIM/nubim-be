package com.soyeon.nubim.domain.userfollow;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.soyeon.nubim.domain.user.User;
import com.soyeon.nubim.domain.user.dto.UserSimpleResponseDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserFollowService {
	private final UserFollowRepository userFollowRepository;

	public void createFollow(User follower, User followee) {
		UserFollow userFollow = UserFollow.builder()
			.follower(follower)
			.followee(followee)
			.build();

		userFollowRepository.save(userFollow);
		follower.getFollowees().add(userFollow);
		followee.getFollowers().add(userFollow);
	}

	public boolean isFollowing(User follower, User followee) {
		if (follower.getFollowees().stream().anyMatch(userFollow -> userFollow.getFollowee().equals(followee))) {
			return true;
		} else {
			return false;
		}
	}

	public void deleteUserFollow(User follower, User followee) {
		UserFollow userFollowToDelete = userFollowRepository.findByFollowerAndFollowee(follower, followee)
			.orElseThrow();
		userFollowRepository.delete(userFollowToDelete);
	}

	public Page<UserSimpleResponseDto> getFollowers(User followee, Pageable pageable) {
		Page<UserFollow> filteredUserFollows = userFollowRepository.findByFollowee(followee, pageable);

		Page<UserSimpleResponseDto> followers = filteredUserFollows.map(userFollow -> {
			User follower = userFollow.getFollower();
			return UserSimpleResponseDto.builder()
				.userId(follower.getUserId())
				.username(follower.getUsername())
				.nickname(follower.getNickname())
				.profileImageUrl(follower.getProfileImageUrl())
				.build();
		});

		return followers;
	}
}
