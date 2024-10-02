package com.soyeon.nubim.domain.post_bookmark;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.soyeon.nubim.domain.post.Post;
import com.soyeon.nubim.domain.post.PostMapper;
import com.soyeon.nubim.domain.post.PostValidator;
import com.soyeon.nubim.domain.post.dto.PostSimpleResponseDto;
import com.soyeon.nubim.domain.post_bookmark.dto.PostBookmarkResponseDto;
import com.soyeon.nubim.domain.post_bookmark.exception.PostBookmarkStatusException;
import com.soyeon.nubim.domain.user.LoggedInUserService;
import com.soyeon.nubim.domain.user.User;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PostBookmarkService {
	private final PostBookmarkRepository postBookmarkRepository;
	private final PostMapper postMapper;
	private final PostValidator postValidator;
	private final LoggedInUserService loggedInUserService;

	@Transactional
	public PostBookmarkResponseDto togglePostBookmark(Long postId) {
		User user = loggedInUserService.getCurrentUser();

		postValidator.validatePostExist(postId);
		Post post = new Post(postId);

		// 이미 북마크가 되어 있으면 삭제
		if (postBookmarkRepository.existsByUserAndPost(user, post)) {
			PostBookmark postBookmark = postBookmarkRepository
				.findByUserAndPost(user, post)
				.orElseThrow(() -> PostBookmarkStatusException.notBookmarked(post.getPostId()));

			postBookmarkRepository.delete(postBookmark);

			return PostBookmarkResponseDto.builder()
				.postId(post.getPostId())
				.message("Successfully deleted postBookmark")
				.bookmarkResult(false)
				.build();
		} else { // 북마크가 안되어 있으면 생성
			PostBookmark postBookmark = PostBookmark.builder()
				.user(user)
				.post(post)
				.build();

			postBookmarkRepository.save(postBookmark);

			return PostBookmarkResponseDto.builder()
				.postId(post.getPostId())
				.message("Successfully bookmarked post")
				.bookmarkResult(true)
				.build();
		}

	}

	public Page<PostSimpleResponseDto> getUserBookmarks(Pageable pageable) {
		User user = new User(loggedInUserService.getCurrentUserId());

		Page<PostBookmark> postBookmarks = postBookmarkRepository.findByUser(user, pageable);

		return postBookmarks.map(
			postBookmark -> postMapper.toPostSimpleResponseDto(postBookmark.getPost())
		);
	}

}