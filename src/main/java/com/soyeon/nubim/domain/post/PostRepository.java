package com.soyeon.nubim.domain.post;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.soyeon.nubim.domain.user.User;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
	List<Post> findByUserUserId(Long userId);

	Page<Post> findByUserUserId(Long postPostId, Pageable pageable);

	@Query("SELECT p FROM Post p WHERE p.user IN " +
		"(SELECT uf.followee FROM UserFollow uf WHERE uf.follower = :user) " +
		"AND p.createdAt >= :criteriaDays AND p.isDeleted = false")
	Page<Post> findRecentPostsByFollowees(User user, LocalDateTime criteriaDays, Pageable pageable);

	@Query(value = "SELECT SETSEED(:seed)", nativeQuery = true)
	void setSeed(Float seed);

	@Query(value = "SELECT p FROM Post p WHERE p.user != :user ORDER BY function('RANDOM')")
	Page<Post> findRandomPostsExceptMine(Pageable pageable, User user);
}
