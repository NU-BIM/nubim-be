package com.soyeon.nubim.domain.postlike;

import org.springframework.stereotype.Service;

import com.soyeon.nubim.domain.post.Post;
import com.soyeon.nubim.domain.post.PostValidator;
import com.soyeon.nubim.domain.postlike.dto.PostLikeResponse;
import com.soyeon.nubim.domain.postlike.dto.PostLikeToggleResponse;
import com.soyeon.nubim.domain.postlike.exception.MultiplePostLikeDeleteException;
import com.soyeon.nubim.domain.user.User;
import com.soyeon.nubim.domain.user.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostLikeService {

	private static final int POST_LIKE_DELETE_SUCCESS = 1;
	private final UserService userService;
	private final PostValidator postValidator;
	private final PostLikeRepository postLikeRepository;

	@Transactional
	public PostLikeResponse togglePostLike(Long postId) {
		Long currentUserId = userService.getCurrentUserId();

		postValidator.validatePostExist(postId);
		userService.validateUserExists(currentUserId);

		// 좋아요 되어 있을 시 좋아요 삭제
		if (postLikeRepository.existsPostLikeByPostAndUser(postId, currentUserId)) {
			int deleteResult = postLikeRepository.deletePostLikeByPostAndUser(postId, currentUserId);

			if (deleteResult != POST_LIKE_DELETE_SUCCESS) {
				throw new MultiplePostLikeDeleteException();
			}
			return new PostLikeToggleResponse("post like removed", false);
		}

		PostLike postLike = PostLike.builder()
			.post(new Post(postId))
			.user(new User(currentUserId))
			.build();
		postLikeRepository.save(postLike);

		return new PostLikeToggleResponse("post like successfully", true);
	}
}
