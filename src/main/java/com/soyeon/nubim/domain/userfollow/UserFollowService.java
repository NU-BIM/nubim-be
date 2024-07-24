package com.soyeon.nubim.domain.userfollow;

import org.springframework.stereotype.Service;

import com.soyeon.nubim.domain.user.User;

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
		follower.getFollowers().add(userFollow);
		followee.getFollowees().add(userFollow);
	}

	public boolean isFollowing(User follower, User followee) {
		if (follower.getFollowers().stream().anyMatch(userFollow -> userFollow.getFollowee().equals(followee))) {
			return true;
		} else {
			return false;
		}
	}
}
