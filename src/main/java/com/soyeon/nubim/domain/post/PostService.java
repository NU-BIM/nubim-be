package com.soyeon.nubim.domain.post;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.soyeon.nubim.domain.post.dto.PostCreateRequestDto;
import com.soyeon.nubim.domain.post.dto.PostCreateResponseDto;
import com.soyeon.nubim.domain.post.dto.PostDetailResponseDto;
import com.soyeon.nubim.domain.post.dto.PostSimpleResponseDto;
import com.soyeon.nubim.domain.post.exceptions.PostNotFoundException;
import com.soyeon.nubim.domain.post.exceptions.UnauthorizedAccessException;
import com.soyeon.nubim.domain.user.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PostService {
	private final PostRepository postRepository;
	private final PostMapper postMapper;

	private static final Random random = new Random();

	public PostDetailResponseDto findPostDetailById(Long id) {
		Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(id));

		return postMapper.toPostDetailResponseDto(post);
	}

	public PostSimpleResponseDto findPostSimpleById(Long id) {
		Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(id));

		return postMapper.toPostSimpleResponseDto(post);
	}

	public Page<PostSimpleResponseDto> findAllPostsByUserIdOrderByCreatedAt(Long userId, Pageable pageable) {
		Page<Post> postList = postRepository.findByUserUserId(userId, pageable);
		return postList
			.map(postMapper::toPostSimpleResponseDto);
	}

	public PostCreateResponseDto createPost(PostCreateRequestDto postCreateRequestDto, User authorUser) {
		Post post = postMapper.toEntity(postCreateRequestDto, authorUser);
		postRepository.save(post);
		return postMapper.toPostCreateResponseDto(post);
	}

	public void deleteById(Long id) {
		postRepository.deleteById(id);
	}

	public Post findPostByIdOrThrow(Long id) {

		return postRepository
			.findById(id)
			.orElseThrow(() -> new PostNotFoundException(id));
	}

	public void validatePostExist(Long postId) {
		if (!postRepository.existsById(postId)) {
			throw new PostNotFoundException(postId);
		}
	}

	public void validatePostOwner(Long postId, User author) {
		Post post = this.findPostByIdOrThrow(postId);

		if (!author.getPosts().contains(post)) {
			throw new UnauthorizedAccessException(postId);
		}
	}

	public Page<PostSimpleResponseDto> findRecentPostsOfFollowees(
		User user, Pageable pageable, int recentCriteriaDays) {
		return postRepository.findRecentPostsByFollowees(user, LocalDateTime.now().minusDays(recentCriteriaDays),
				pageable)
			.map(postMapper::toPostSimpleResponseDto);
	}

	public Page<PostSimpleResponseDto> findRandomPosts(Pageable pageable, Float randomSeed, User user) {
		float seed = getOrGenerateRandomSeed(randomSeed);
		postRepository.setSeed(seed);

		CustomPageImpl<PostSimpleResponseDto> customPage = new CustomPageImpl<>(
			postRepository.findRandomPostsExceptMine(pageable, user).map(postMapper::toPostSimpleResponseDto));
		customPage.setRandomSeed(seed);

		return customPage;
	}

	private Float getOrGenerateRandomSeed(Float seed) {
		if (seed == null) {
			return random.nextFloat();
		}
		return seed;
	}
}
