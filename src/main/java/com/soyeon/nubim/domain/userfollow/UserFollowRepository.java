package com.soyeon.nubim.domain.userfollow;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.soyeon.nubim.domain.user.User;

@Repository
public interface UserFollowRepository extends JpaRepository<UserFollow, Long> {

	@Query("SELECT a FROM UserFollow AS a WHERE (a.follower = :follower and a.followee = :followee)")
	Optional<UserFollow> findByFollowerAndFollowee(User follower, User followee);

	Page<UserFollow> findByFollowee(User followee, Pageable pageable);

	Page<UserFollow> findByFollower(User follower, Pageable pageable);
}
