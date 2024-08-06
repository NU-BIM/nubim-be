package com.soyeon.nubim.domain.userfollow.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class FollowingStatusException extends ResponseStatusException {
	public FollowingStatusException(String message) {
		super(HttpStatus.BAD_REQUEST, message);
	}

	public static FollowingStatusException followYourself() {
		return new FollowingStatusException("Cannot follow yourself");
	}

	public static FollowingStatusException alreadyFollowing(String followeeNickname) {
		return new FollowingStatusException("You are already following " + followeeNickname);
	}

	public static FollowingStatusException notFollowing(String followeeNickname) {
		return new FollowingStatusException("You are NOT following " + followeeNickname);
	}

}
