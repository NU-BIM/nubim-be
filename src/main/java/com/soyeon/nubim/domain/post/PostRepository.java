package com.soyeon.nubim.domain.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserUserId(Long userId);

    List<Post> findByUserUserIdOrderByCreatedAtDesc(Long userId);

    List<Post> findByUserUserIdOrderByCreatedAtAsc(Long userId);
}
