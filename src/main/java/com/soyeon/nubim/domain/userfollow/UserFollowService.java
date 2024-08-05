package com.soyeon.nubim.domain.userfollow;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.soyeon.nubim.domain.user.User;
import com.soyeon.nubim.domain.user.UserMapper;
import com.soyeon.nubim.domain.user.UserService;
import com.soyeon.nubim.domain.user.dto.UserSimpleResponseDto;
import com.soyeon.nubim.domain.userfollow.dto.FollowUserResponseDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserFollowService {
	private final UserFollowRepository userFollowRepository;
	private final UserService userService;
	private final UserMapper userMapper;

	public FollowUserResponseDto createFollow(String nickname) {
		User followee = userService.getUserByNickname(nickname);
		User follower = userService.getCurrentUser();

		if (follower.equals(followee)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot follow yourself");
		}

		if (this.isFollowing(follower, followee)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are already following");
		}

		UserFollow userFollow = UserFollow.builder()
			.follower(follower)
			.followee(followee)
			.build();

		userFollowRepository.save(userFollow);
		userFollow.addFollowerAndFolloweeByUserFollow();

		return FollowUserResponseDto.builder()
			.followerId(userFollow.getFollower().getUserId())
			.followeeId(userFollow.getFollowee().getUserId())
			.message("Successfully followed")
			.build();
	}

	public boolean isFollowing(User follower, User followee) {
		if (follower.getFollowees().stream().anyMatch(userFollow -> userFollow.getFollowee().equals(followee))) {
			return true;
		} else {
			return false;
		}
	}

	public FollowUserResponseDto deleteUserFollow(String nickname) {
		User followee = userService.getUserByNickname(nickname);
		User follower = userService.getCurrentUser();

		if (!isFollowing(follower, followee)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are not following");
		}
		UserFollow userFollowToDelete = userFollowRepository.findByFollowerAndFollowee(follower, followee)
			.orElseThrow();
		userFollowRepository.delete(userFollowToDelete);
		userFollowToDelete.deleteFollowerAndFolloweeByUserFollow();

		return FollowUserResponseDto.builder()
			.followerId(follower.getUserId())
			.followeeId(followee.getUserId())
			.message("Successfully unfollowed")
			.build();
	}

	public Page<UserSimpleResponseDto> getFollowers(Pageable pageable) {
		User followee = userService.getCurrentUser();

		Page<UserFollow> filteredUserFollows = userFollowRepository.findByFollowee(followee, pageable);

		return filteredUserFollows.map(
			userFollow -> userMapper.toUserSimpleResponseDto(userFollow.getFollower()));
	}

	public Page<UserSimpleResponseDto> getFollowees(Pageable pageable) {
		User follower = userService.getCurrentUser();

		Page<UserFollow> filteredUserFollows = userFollowRepository.findByFollower(follower, pageable);

		return filteredUserFollows.map(
			userFollow -> userMapper.toUserSimpleResponseDto(userFollow.getFollowee()));
	}
}
