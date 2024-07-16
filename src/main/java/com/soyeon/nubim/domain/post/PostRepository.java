package com.soyeon.nubim.domain.post;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
	List<Post> findByUserUserId(Long userId);

	Page<Post> findByUserUserId(Long postPostId, Pageable pageable);
}
