package com.soyeon.nubim.domain.userfollow;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.soyeon.nubim.domain.user.User;

@Repository
public interface UserFollowRepository extends JpaRepository<UserFollow, Long> {

	@Query("SELECT a FROM UserFollow AS a WHERE (a.follower = :follower and a.followee = :followee)")
	Optional<UserFollow> findByFollowerAndFollowee(User follower, User followee);

	Boolean existsByFollowerAndFollowee(User follower, User followee);

	Page<UserFollow> findByFollowee(User followee, Pageable pageable);

	Page<UserFollow> findByFollower(User follower, Pageable pageable);

	@Modifying
	@Query("UPDATE UserFollow uf SET uf.isDeleted = true WHERE uf.follower.userId = :userId")
	int deleteFollowerByUserId(Long userId);

	@Modifying
	@Query("UPDATE UserFollow uf SET uf.isDeleted = true WHERE uf.followee.userId = :userId")
	int deleteFolloweeByUserId(Long userId);

	@Modifying
	@Query("UPDATE UserFollow uf SET uf.isDeleted = true "
		+ "WHERE (uf.follower.userId = :blockingUserId AND uf.followee.userId = :blockedUserId) "
		+ "OR (uf.follower.userId = :blockedUserId AND uf.followee.userId = :blockingUserId)")
	int deleteFollowByUserId(Long blockingUserId, Long blockedUserId);
}
