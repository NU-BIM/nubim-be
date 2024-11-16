package com.soyeon.nubim.domain.post;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.soyeon.nubim.domain.user.User;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
	List<Post> findByUserUserId(Long userId);

	Page<Post> findByUser(User user, Pageable pageable);

	@Query("SELECT p FROM Post p WHERE p.user IN " +
		   "(SELECT uf.followee FROM UserFollow uf WHERE uf.follower = :user) " +
		   "AND p.createdAt >= :criteriaDays AND p.isDeleted = false")
	Page<Post> findRecentPostsByFollowees(User user, LocalDateTime criteriaDays, Pageable pageable);

	@Query(value = "SELECT SETSEED(:seed)", nativeQuery = true)
	void setSeed(Float seed);

	@Query(value = "SELECT p FROM Post p WHERE p.user != :user "
				   + "AND NOT EXISTS (SELECT ub FROM UserBlock ub WHERE ub.blockingUser = :user AND ub.blockedUser = p.user) "
				   + "AND NOT EXISTS (SELECT ub FROM UserBlock ub WHERE ub.blockingUser = p.user AND ub.blockedUser = :user) "
				   + "ORDER BY function('RANDOM')")
	Page<Post> findRandomPostsExceptMine(Pageable pageable, User user);

	@Query("""
		SELECT DISTINCT p FROM Post p
		WHERE p.album IN (
		    SELECT l.album
		    FROM Location l
		    WHERE l.placeName LIKE %:query%
		    AND l.album.postLinked = true
		    GROUP BY l.album
		)
		OR (p.postTitle LIKE %:query% OR p.postContent LIKE %:query%)
		""")
	Page<Post> findByAlbumLocationPlaceNameOrPostTitleOrPostContent(String query, Pageable pageable);

	@Modifying
	@Query("UPDATE Post p SET p.isDeleted = true WHERE p.album.albumId = :albumId")
	void deletePostByDeletedAlbumId(Long albumId);

	@Modifying
	@Query("UPDATE Post p SET p.isDeleted = true WHERE p.user.userId = :userId")
	int deletePostByUserId(Long userId);
}
