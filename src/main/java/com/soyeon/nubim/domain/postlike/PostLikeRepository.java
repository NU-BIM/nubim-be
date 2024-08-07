package com.soyeon.nubim.domain.postlike;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

	@Query("SELECT EXISTS (FROM PostLike pl WHERE pl.post.postId = :postId AND pl.user.userId = :userId)")
	boolean existsPostLikeByPostAndUser(Long postId, Long userId);

	@Modifying
	@Query("DELETE FROM PostLike pl WHERE pl.post.postId = :postId AND pl.user.userId = :userId")
	int deletePostLikeByPostAndUser(Long postId, Long userId);
}
