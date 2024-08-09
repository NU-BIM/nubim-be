package com.soyeon.nubim.domain.post;

import org.springframework.stereotype.Service;

import com.soyeon.nubim.domain.post.exceptions.PostNotFoundException;
import com.soyeon.nubim.domain.post.exceptions.UnauthorizedPostAccessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostValidator {

	private final PostRepository postRepository;

	public void validatePostExist(Long postId) {
		if (!postRepository.existsById(postId)) {
			throw new PostNotFoundException(postId);
		}
	}

	public void validatePostOwner(Post post, Long userId) {
		Long postOwnerId = post.getUser().getUserId();

		if (!postOwnerId.equals(userId)) {
			throw new UnauthorizedPostAccessException(post.getPostId());
		}
	}
}
