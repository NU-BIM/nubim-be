package com.soyeon.nubim.domain.user_block;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.soyeon.nubim.domain.user.User;

@Repository
public interface UserBlockRepository extends JpaRepository<UserBlock, Long> {

	@Query("SELECT EXISTS( SELECT 1 FROM UserBlock ub"
		+ " WHERE ub.blockingUser = :blockingUser AND ub.blockedUser = :blockedUser)")
	boolean existsByBlockingUserAndBlockedUser(User blockingUser, User blockedUser);

	@Query("SELECT ub FROM UserBlock ub WHERE ub.blockingUser = :blockingUser")
	List<UserBlock> findBlockedUsersByBlockingUser(User blockingUser);

	@Modifying
	@Query("DELETE FROM UserBlock ub WHERE ub.blockingUser = :blockingUser AND ub.blockedUser = :blockedUser")
	int deleteByBlockingUserAndBlockedUser(User blockingUser, User blockedUser);
}
