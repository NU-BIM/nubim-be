package com.soyeon.nubim.domain.user_block;

import org.springframework.stereotype.Service;

import com.soyeon.nubim.domain.user.User;
import com.soyeon.nubim.domain.user_block.exception.AlreadyBlockedException;
import com.soyeon.nubim.domain.user_block.exception.BlockedUserAccessDeniedException;
import com.soyeon.nubim.domain.user_block.exception.SelfBlockException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserBlockValidator {
	private final UserBlockRepository userBlockRepository;

	public void checkBlockRelation(User currentUser, User targetUser) {
		if (userBlockRepository.existsBlockRelationBetweenUser(currentUser, targetUser)) {
			throw new BlockedUserAccessDeniedException();
		}
	}

	protected void checkSelfBlock(User blockingUser, User blockedUser) {
		if (blockingUser.getUserId().equals(blockedUser.getUserId())) {
			throw new SelfBlockException();
		}
	}

	protected void validateUserBlockNotExists(User blockingUser, User blockedUser) {
		if (userBlockRepository.existsByBlockingUserAndBlockedUser(blockingUser, blockedUser)) {
			throw new AlreadyBlockedException();
		}
	}
}
