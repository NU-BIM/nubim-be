package com.soyeon.nubim.domain.post;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.soyeon.nubim.domain.album.Album;
import com.soyeon.nubim.domain.album.dto.AlbumResponseDto;
import com.soyeon.nubim.domain.album.mapper.AlbumMapper;
import com.soyeon.nubim.domain.comment.dto.CommentResponseDto;
import com.soyeon.nubim.domain.post.dto.PostCreateRequestDto;
import com.soyeon.nubim.domain.post.dto.PostCreateResponseDto;
import com.soyeon.nubim.domain.post.dto.PostDetailResponseDto;
import com.soyeon.nubim.domain.post.dto.PostMainResponseDto;
import com.soyeon.nubim.domain.post.dto.PostSimpleResponseDto;
import com.soyeon.nubim.domain.post_bookmark.PostBookmarkRepository;
import com.soyeon.nubim.domain.postlike.PostLike;
import com.soyeon.nubim.domain.postlike.PostLikeRepository;
import com.soyeon.nubim.domain.user.LoggedInUserService;
import com.soyeon.nubim.domain.user.User;
import com.soyeon.nubim.domain.user.dto.UserResponseDto;
import com.soyeon.nubim.domain.user.dto.UserSimpleResponseDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PostMapper {

	private final AlbumMapper albumMapper;
	private final PostBookmarkRepository postBookmarkRepository;
	private final PostLikeRepository postLikeRepository;
	private final LoggedInUserService loggedInUserService;

	public Post toEntity(PostCreateRequestDto postCreateRequestDto, User authorUser, Album linkedAlbum) {
		return Post.builder()
			.postTitle(postCreateRequestDto.getPostTitle())
			.postContent(postCreateRequestDto.getPostContent())
			.album(linkedAlbum)
			.user(authorUser)
			.build();
	}

	public PostCreateResponseDto toPostCreateResponseDto(Post post) {
		return PostCreateResponseDto.builder()
			.postId(post.getPostId())
			.userId(post.getUser().getUserId())
			.albumId(post.getAlbum().getAlbumId())
			.postTitle(post.getPostTitle())
			.postContent(post.getPostContent())
			.build();
	}

	public PostDetailResponseDto toPostDetailResponseDto(Post post) {
		return PostDetailResponseDto.builder()
			.postId(post.getPostId())
			.postTitle(post.getPostTitle())
			.postContent(post.getPostContent())
			.user(createUserSimpleResponseDto(post.getUser()))
			.album(albumMapper.toAlbumReadResponseDto(post.getAlbum()))
			.build();
	}

	public PostSimpleResponseDto toPostSimpleResponseDto(Post post) {
		UserSimpleResponseDto userSimpleResponseDto = UserSimpleResponseDto.builder()
			.username(post.getUser().getUsername())
			.profileImageUrl(post.getUser().getProfileImageUrl())
			.nickname(post.getUser().getNickname())
			.build();

		return PostSimpleResponseDto.builder()
			.postId(post.getPostId())
			.postTitle(post.getPostTitle())
			.postContent(post.getPostContent())
			.numberOfComments((long)post.getComments().size())
			.user(userSimpleResponseDto)
			.albumId(post.getAlbum().getAlbumId())
			.createdAt(post.getCreatedAt())
			.updatedAt(post.getUpdatedAt())
			.build();
	}

	public PostMainResponseDto toPostMainResponseDto(
		Post post, CommentResponseDto representativeComment, long numberOfComments) {

		User currentUser = loggedInUserService.getCurrentUser();
		UserSimpleResponseDto user = createUserSimpleResponseDto(post.getUser());
		AlbumResponseDto album = albumMapper.toAlbumReadResponseDto(post.getAlbum());
		List<UserResponseDto> postLikeUsers = createPostLikeUsers(post.getPostLikes());
		Boolean isBookmarked = postBookmarkRepository.existsByUserAndPost(currentUser, post);
		Boolean isLiked = postLikeRepository.existsPostLikeByPostAndUser(post.getPostId(), currentUser.getUserId());

		return PostMainResponseDto.builder()
			.postId(post.getPostId())
			.postTitle(post.getPostTitle())
			.postContent(post.getPostContent())
			.user(user)
			.createdAt(post.getCreatedAt())
			.updatedAt(post.getUpdatedAt())
			.album(album)
			.postLikeUsers(postLikeUsers)
			.numberOfPostLikes((long)postLikeUsers.size())
			.isBookmarked(isBookmarked)
			.isLiked(isLiked)
			.representativeComment(representativeComment)
			.numberOfComments(numberOfComments)
			.build();
	}

	public List<PostDetailResponseDto> toPostDetailResponseDtos(List<Post> posts) {
		List<PostDetailResponseDto> postDetailResponseDtos = new ArrayList<>(posts.size());
		for (Post post : posts) {
			postDetailResponseDtos.add(toPostDetailResponseDto(post));
		}
		return postDetailResponseDtos;
	}

	private UserSimpleResponseDto createUserSimpleResponseDto(User user) {
		return UserSimpleResponseDto.builder()
			.username(user.getUsername())
			.profileImageUrl(user.getProfileImageUrl())
			.nickname(user.getNickname())
			.build();
	}

	private List<UserResponseDto> createPostLikeUsers(List<PostLike> postLikes) {
		List<UserResponseDto> postLikeUsers = new ArrayList<>();
		for (PostLike postLike : postLikes) {
			User postLikeUser = postLike.getUser();
			postLikeUsers.add(createUserSimpleResponseDto(postLikeUser));
		}
		return postLikeUsers;
	}

}
