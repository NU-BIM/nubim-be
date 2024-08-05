package com.soyeon.nubim.domain.post;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.soyeon.nubim.domain.album.Album;
import com.soyeon.nubim.domain.album.AlbumNotFoundException;
import com.soyeon.nubim.domain.album.AlbumService;
import com.soyeon.nubim.domain.comment.Comment;
import com.soyeon.nubim.domain.comment.CommentMapper;
import com.soyeon.nubim.domain.comment.dto.CommentResponseDto;
import com.soyeon.nubim.domain.post.dto.PostCreateRequestDto;
import com.soyeon.nubim.domain.post.dto.PostCreateResponseDto;
import com.soyeon.nubim.domain.post.dto.PostDetailResponseDto;
import com.soyeon.nubim.domain.post.dto.PostMainResponseDto;
import com.soyeon.nubim.domain.post.dto.PostSimpleResponseDto;
import com.soyeon.nubim.domain.post.exceptions.PostNotFoundException;
import com.soyeon.nubim.domain.post.exceptions.UnauthorizedAccessException;
import com.soyeon.nubim.domain.user.User;
import com.soyeon.nubim.domain.user.UserMapper;
import com.soyeon.nubim.domain.user.dto.UserSimpleResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {
	private final PostRepository postRepository;
	private final PostMapper postMapper;
	private final AlbumService albumService;

	private static final Random random = new Random();
	private final CommentMapper commentMapper;
	private final UserMapper userMapper;

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
		Album linkedAlbum = albumService.findById(postCreateRequestDto.getAlbumId())
			.orElseThrow(() -> new AlbumNotFoundException(postCreateRequestDto.getAlbumId()));
		Post post = postMapper.toEntity(postCreateRequestDto, authorUser, linkedAlbum);

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

	public Page<PostMainResponseDto> findRecentPostsOfFollowees(
		User user, Pageable pageable, int recentCriteriaDays) {
		return postRepository.findRecentPostsByFollowees(user, LocalDateTime.now().minusDays(recentCriteriaDays),
				pageable)
			.map(post -> postMapper.toPostMainResponseDto(post, findRecentCommentByPostOrNull(post)));
	}

	public Page<PostMainResponseDto> findRandomPosts(Pageable pageable, Float randomSeed, User user) {
		float seed = getOrGenerateRandomSeed(randomSeed);
		postRepository.setSeed(seed);

		CustomPageImpl<PostMainResponseDto> customPage = new CustomPageImpl<>(
			postRepository.findRandomPostsExceptMine(pageable, user)
				.map(post -> postMapper.toPostMainResponseDto(post, findRecentCommentByPostOrNull(post)))
		);
		customPage.setRandomSeed(seed);

		return customPage;
	}

	private Float getOrGenerateRandomSeed(Float seed) {
		if (seed == null) {
			return random.nextFloat();
		}
		return seed;
	}

	private CommentResponseDto findRecentCommentByPostOrNull(Post post) {
		Comment lastCommentByPost = post.getComments().stream().findFirst().orElse(null);
		if (lastCommentByPost == null) {
			return null;
		} else {
			UserSimpleResponseDto userSimpleResponseDto = userMapper.toUserSimpleResponseDto(
				lastCommentByPost.getUser());
			return commentMapper.toCommentResponseDto(lastCommentByPost, userSimpleResponseDto);
		}
	}

	public Page<PostSimpleResponseDto> searchPostsByTitleOrContent(Pageable pageable, String query) {
		return postRepository.findByPostTitleContainingOrPostContentContaining(query, query, pageable)
			.map(postMapper::toPostSimpleResponseDto);
	}
}
