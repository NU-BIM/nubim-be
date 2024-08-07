package com.soyeon.nubim.domain.postlike;

import org.springframework.stereotype.Service;

import com.soyeon.nubim.domain.post.Post;
import com.soyeon.nubim.domain.post.PostService;
import com.soyeon.nubim.domain.postlike.dto.PostLikeResponse;
import com.soyeon.nubim.domain.user.User;
import com.soyeon.nubim.domain.user.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostLikeService {

	private final UserService userService;
	private final PostService postService;
	private final PostLikeRepository postLikeRepository;

	@Transactional
	public PostLikeResponse createPostLike(Long postId) {
		Long currentUserId = userService.getCurrentUserId();

		postService.validatePostExist(postId);
		userService.validateUserExists(currentUserId);

		PostLike postLike = PostLike.builder()
			.post(new Post(postId, currentUserId))
			.user(new User(currentUserId))
			.build();
		postLikeRepository.save(postLike);

		return new PostLikeResponse("post like successfully");
	}
}
