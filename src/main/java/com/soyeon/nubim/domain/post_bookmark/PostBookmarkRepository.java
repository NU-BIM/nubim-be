package com.soyeon.nubim.domain.post_bookmark;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.soyeon.nubim.domain.post.Post;
import com.soyeon.nubim.domain.user.User;

public interface PostBookmarkRepository extends JpaRepository<PostBookmark, Long> {
	Optional<PostBookmark> findByUserAndPost(User user, Post post);

	boolean existsByUserAndPost(User user, Post post);

	Page<PostBookmark> findByUser(User user, Pageable pageable);
}