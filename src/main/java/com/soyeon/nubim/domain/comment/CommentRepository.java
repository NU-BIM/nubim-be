package com.soyeon.nubim.domain.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
	Page<Comment> findByPostPostId(Long postId, Pageable pageable);

	@Query("SELECT COUNT(*) FROM Comment c WHERE c.post.postId = :postId AND c.isDeleted = false")
	long countCommentByPostId(Long postId);

	@Modifying
	@Query("UPDATE Comment c SET c.isDeleted = true WHERE c.user.userId = :userId")
	int deleteCommentByUserId(Long userId);
}
