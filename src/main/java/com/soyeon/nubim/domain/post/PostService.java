package com.soyeon.nubim.domain.post;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.soyeon.nubim.domain.album.Album;
import com.soyeon.nubim.domain.album.AlbumService;
import com.soyeon.nubim.domain.album.AlbumValidator;
import com.soyeon.nubim.domain.comment.Comment;
import com.soyeon.nubim.domain.comment.CommentMapper;
import com.soyeon.nubim.domain.comment.CommentService;
import com.soyeon.nubim.domain.comment.dto.CommentResponseDto;
import com.soyeon.nubim.domain.post.dto.PostCreateRequestDto;
import com.soyeon.nubim.domain.post.dto.PostCreateResponseDto;
import com.soyeon.nubim.domain.post.dto.PostDetailResponseDto;
import com.soyeon.nubim.domain.post.dto.PostMainResponseDto;
import com.soyeon.nubim.domain.post.dto.PostSimpleResponseDto;
import com.soyeon.nubim.domain.post.exceptions.PostNotFoundException;
import com.soyeon.nubim.domain.user.User;
import com.soyeon.nubim.domain.user.UserMapper;
import com.soyeon.nubim.domain.user.dto.UserSimpleResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {
	private final PostRepository postRepository;
	private final PostMapper postMapper;
	private final PostValidator postValidator;
	private final AlbumService albumService;
	private final AlbumValidator albumValidator;
	private final CommentService commentService;

	private static final Random random = new Random();
	private final CommentMapper commentMapper;
	private final UserMapper userMapper;

	public PostCreateResponseDto createPost(PostCreateRequestDto postCreateRequestDto, User authorUser) {
		Album linkedAlbum = albumService.findById(postCreateRequestDto.getAlbumId());
		albumValidator.validateAlbumOwner(linkedAlbum.getAlbumId(), authorUser.getUserId());

		Post post = postMapper.toEntity(postCreateRequestDto, authorUser, linkedAlbum);
		post.linkAlbum(linkedAlbum);

		postRepository.save(post);
		return postMapper.toPostCreateResponseDto(post);
	}

	public Post findPostByIdOrThrow(Long id) {
		return postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(id));
	}

	public PostSimpleResponseDto findPostSimpleById(Long id) {
		Post post = findPostByIdOrThrow(id);

		return postMapper.toPostSimpleResponseDto(post);
	}

	public PostDetailResponseDto findPostDetailById(Long id) {
		Post post = findPostByIdOrThrow(id);

		return postMapper.toPostDetailResponseDto(post);
	}

	public PostMainResponseDto findPostMainById(Long id) {
		Post post = findPostByIdOrThrow(id);

		long numberOfComments = commentService.getCommentCountByPostId(post.getPostId());
		return postMapper.toPostMainResponseDto(post, findRecentCommentByPostOrNull(post), numberOfComments);
	}

	public Page<PostMainResponseDto> searchPostsByTitleOrContent(Pageable pageable, String query) {
		return postRepository.findByPostTitleContainingOrPostContentContaining(query, query, pageable)
			.map(post -> {
				long numberOfComments = commentService.getCommentCountByPostId(post.getPostId());
				return postMapper.toPostMainResponseDto(post, findRecentCommentByPostOrNull(post), numberOfComments);
			});
	}

	public Page<PostMainResponseDto> findAllPostsByUserOrderByCreatedAt(User user, Pageable pageable) {
		Page<Post> postList = postRepository.findByUser(user, pageable);
		return postList
			.map(post -> {
				long numberOfComments = commentService.getCommentCountByPostId(post.getPostId());
				return postMapper.toPostMainResponseDto(post, findRecentCommentByPostOrNull(post), numberOfComments);
			});
	}

	public Page<PostMainResponseDto> findRecentPostsOfFollowees(User user, Pageable pageable, int recentCriteriaDays) {
		return postRepository.findRecentPostsByFollowees(user, LocalDateTime.now().minusDays(recentCriteriaDays),
				pageable)
			.map(post -> {
				long numberOfComments = commentService.getCommentCountByPostId(post.getPostId());
				return postMapper.toPostMainResponseDto(post, findRecentCommentByPostOrNull(post), numberOfComments);
			});
	}

	public Page<PostMainResponseDto> findRandomPosts(Pageable pageable, Float randomSeed, User user) {
		float seed = getOrGenerateRandomSeed(randomSeed);
		postRepository.setSeed(seed);

		CustomPageImpl<PostMainResponseDto> customPage = new CustomPageImpl<>(
			postRepository.findRandomPostsExceptMine(pageable, user)
				.map(post -> {
					long numberOfComments = commentService.getCommentCountByPostId(post.getPostId());
					return
						postMapper.toPostMainResponseDto(post, findRecentCommentByPostOrNull(post), numberOfComments);
				})
		);
		customPage.setRandomSeed(seed);

		return customPage;
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

	public void deleteById(Long postId, Long userId) {
		Post post = findPostByIdOrThrow(postId);
		postValidator.validatePostOwner(post, userId);

		post.unlinkAlbum();

		postRepository.deleteById(postId);
	}

	private Float getOrGenerateRandomSeed(Float seed) {
		if (seed == null) {
			return random.nextFloat();
		}
		return seed;
	}
}
