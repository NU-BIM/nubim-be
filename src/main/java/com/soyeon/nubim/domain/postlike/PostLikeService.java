package com.soyeon.nubim.domain.postlike;

import org.springframework.stereotype.Service;

import com.soyeon.nubim.domain.post.Post;
import com.soyeon.nubim.domain.post.PostService;
import com.soyeon.nubim.domain.postlike.dto.PostLikeCreateResponse;
import com.soyeon.nubim.domain.postlike.exception.MultiplePostLikeDeleteException;
import com.soyeon.nubim.domain.user.User;
import com.soyeon.nubim.domain.user.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostLikeService {

	public static final int POST_LIKE_DELETE_SUCCESS = 1;
	private final UserService userService;
	private final PostService postService;
	private final PostLikeRepository postLikeRepository;

	@Transactional
	public PostLikeCreateResponse togglePostLike(Long postId) {
		Long currentUserId = userService.getCurrentUserId();

		postService.validatePostExist(postId);
		userService.validateUserExists(currentUserId);

		// 좋아요 되어 있을 시 좋아요 삭제
		if (postLikeRepository.existsPostLikeByPostAndUser(postId, currentUserId)) {
			int deleteResult = postLikeRepository.deletePostLikeByPostAndUser(postId, currentUserId);

			if (deleteResult != POST_LIKE_DELETE_SUCCESS) {
				throw new MultiplePostLikeDeleteException();
			}
			return new PostLikeCreateResponse("post like removed");
		}

		PostLike postLike = PostLike.builder()
			.post(new Post(postId, currentUserId))
			.user(new User(currentUserId))
			.build();
		postLikeRepository.save(postLike);

		return new PostLikeCreateResponse("post like successfully");
	}
}
