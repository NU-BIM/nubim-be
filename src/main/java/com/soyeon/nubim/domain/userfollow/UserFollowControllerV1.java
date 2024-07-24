package com.soyeon.nubim.domain.userfollow;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.soyeon.nubim.domain.user.User;
import com.soyeon.nubim.domain.user.UserService;
import com.soyeon.nubim.domain.userfollow.dto.FollowUserResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/follows")
public class UserFollowControllerV1 {
	final UserFollowService userFollowService;
	final UserService userService;

	@Operation(description = "로그인된 유저가 해당 userId를 팔로우")
	@PostMapping("/{userId}")
	public ResponseEntity<FollowUserResponseDto> followUser(@PathVariable Long userId) {
		userService.validateUserExists(userId);
		this.validateFollowNotMyself(userId);

		User follower = userService.getCurrentUser();
		User followee = userService.findUserByIdOrThrow(userId);

		if (userFollowService.isFollowing(follower, followee)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are already following");
		}

		userFollowService.createFollow(follower, followee);

		FollowUserResponseDto followUserResponseDto = FollowUserResponseDto.builder()
			.followerId(follower.getUserId())
			.followeeId(followee.getUserId())
			.message("Successfully followed")
			.build();

		return ResponseEntity
			.created(URI.create("")) // TODO : 팔로잉 조회 uri 추가
			.body(followUserResponseDto);
	}

	private void validateFollowNotMyself(Long followee) {
		if (userService.getCurrentUserId().equals(followee)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot follow yourself");
		}
	}

	@DeleteMapping("/{userId}")
	public ResponseEntity<Void> unfollowUser(@PathVariable Long userId) {
		userService.validateUserExists(userId);

		User follower = userService.getCurrentUser();
		User followee = userService.findUserByIdOrThrow(userId);

		if (!userFollowService.isFollowing(follower, followee)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You are not following");
		}
		userFollowService.deleteUserFollow(follower, followee);

		return ResponseEntity.ok().build();
	}
}