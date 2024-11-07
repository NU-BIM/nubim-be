package com.soyeon.nubim.domain.userfollow;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.soyeon.nubim.domain.user.LoggedInUserService;
import com.soyeon.nubim.domain.user.User;
import com.soyeon.nubim.domain.user.UserMapper;
import com.soyeon.nubim.domain.user.UserService;
import com.soyeon.nubim.domain.user.dto.UserSimpleResponseDto;
import com.soyeon.nubim.domain.user_block.UserBlockValidator;
import com.soyeon.nubim.domain.userfollow.dto.FollowUserResponseDto;
import com.soyeon.nubim.domain.userfollow.exception.FollowingStatusException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserFollowService {
	private final UserFollowRepository userFollowRepository;
	private final UserService userService;
	private final UserMapper userMapper;
	private final LoggedInUserService loggedInUserService;
	private final UserBlockValidator userBlockValidator;

	public FollowUserResponseDto createFollow(String nickname) {
		User followee = userService.getUserByNickname(nickname);
		User follower = loggedInUserService.getCurrentUser();

		if (follower.equals(followee)) {
			throw FollowingStatusException.followYourself();
		}

		if (this.isFollowing(follower, followee)) {
			throw FollowingStatusException.alreadyFollowing(nickname);
		}
		userBlockValidator.checkBlockRelation(follower, followee);

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
		User follower = loggedInUserService.getCurrentUser();

		if (!isFollowing(follower, followee)) {
			throw FollowingStatusException.notFollowing(nickname);
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

	public void deleteFollowBetweenUsers(User blockingUser, User blockedUser) {
		Long blockingUserId = blockingUser.getUserId();
		Long blockedUserId = blockedUser.getUserId();

		userFollowRepository.deleteFollowByUserId(blockingUserId);
		userFollowRepository.deleteFollowByUserId(blockedUserId);
	}

	public Page<UserSimpleResponseDto> getFollowers(Pageable pageable) {
		User followee = loggedInUserService.getCurrentUser();

		Page<UserFollow> filteredUserFollows = userFollowRepository.findByFollowee(followee, pageable);

		return filteredUserFollows.map(
			userFollow -> userMapper.toUserSimpleResponseDto(userFollow.getFollower()));
	}

	public Page<UserSimpleResponseDto> getFollowees(Pageable pageable) {
		User follower = loggedInUserService.getCurrentUser();

		Page<UserFollow> filteredUserFollows = userFollowRepository.findByFollower(follower, pageable);

		return filteredUserFollows.map(
			userFollow -> userMapper.toUserSimpleResponseDto(userFollow.getFollowee()));
	}
}
