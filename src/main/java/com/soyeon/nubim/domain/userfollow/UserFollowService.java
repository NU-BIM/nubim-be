package com.soyeon.nubim.domain.userfollow;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.soyeon.nubim.domain.user.User;
import com.soyeon.nubim.domain.user.UserMapper;
import com.soyeon.nubim.domain.user.UserService;
import com.soyeon.nubim.domain.user.dto.UserSimpleResponseDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserFollowService {
	private final UserFollowRepository userFollowRepository;
	private final UserService userService;
	private final UserMapper userMapper;

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

		return filteredUserFollows.map(
			userFollow -> userMapper.toUserSimpleResponseDto(userFollow.getFollower()));
	}

	public Page<UserSimpleResponseDto> getFollowees(User follower, Pageable pageable) {
		Page<UserFollow> filteredUserFollows = userFollowRepository.findByFollower(follower, pageable);

		return filteredUserFollows.map(
			userFollow -> userMapper.toUserSimpleResponseDto(userFollow.getFollowee()));
	}
}
