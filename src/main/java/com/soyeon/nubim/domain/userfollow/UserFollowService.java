package com.soyeon.nubim.domain.userfollow;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.soyeon.nubim.domain.user.User;
import com.soyeon.nubim.domain.user.UserService;
import com.soyeon.nubim.domain.user.dto.UserSimpleResponseDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserFollowService {
	private final UserFollowRepository userFollowRepository;
	private final UserService userService;

	public void createFollow(User follower, User followee) {
		UserFollow userFollow = UserFollow.builder()
			.follower(follower)
			.followee(followee)
			.build();

		userFollowRepository.save(userFollow);
		userService.addFollowerAndFolloweeByUserFollow(userFollow);
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
		userService.deleteFollowerAndFolloweeByUserFollow(userFollowToDelete);
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

	public Page<UserSimpleResponseDto> getFollowees(User follower, Pageable pageable) {
		Page<UserFollow> filteredUserFollows = userFollowRepository.findByFollower(follower, pageable);

		Page<UserSimpleResponseDto> followees = filteredUserFollows.map(userFollow -> {
			User followee = userFollow.getFollowee();
			return UserSimpleResponseDto.builder()
				.userId(followee.getUserId())
				.username(followee.getUsername())
				.nickname(followee.getNickname())
				.profileImageUrl(followee.getProfileImageUrl())
				.build();
		});

		return followees;
	}
}
