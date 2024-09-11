package com.soyeon.nubim.domain.post;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.soyeon.nubim.domain.album.Album;
import com.soyeon.nubim.domain.album.Location;
import com.soyeon.nubim.domain.album.dto.AlbumReadResponseDto;
import com.soyeon.nubim.domain.album.dto.AlbumSimpleResponse;
import com.soyeon.nubim.domain.album.dto.LocationReadResponseDto;
import com.soyeon.nubim.domain.album.mapper.AlbumMapper;
import com.soyeon.nubim.domain.album.mapper.LocationMapper;
import com.soyeon.nubim.domain.comment.Comment;
import com.soyeon.nubim.domain.comment.dto.CommentResponseDto;
import com.soyeon.nubim.domain.post.dto.PostCreateRequestDto;
import com.soyeon.nubim.domain.post.dto.PostCreateResponseDto;
import com.soyeon.nubim.domain.post.dto.PostDetailResponseDto;
import com.soyeon.nubim.domain.post.dto.PostMainResponseDto;
import com.soyeon.nubim.domain.post.dto.PostSimpleResponseDto;
import com.soyeon.nubim.domain.postlike.PostLike;
import com.soyeon.nubim.domain.user.User;
import com.soyeon.nubim.domain.user.dto.UserSimpleResponseDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PostMapper {

	private final LocationMapper locationMapper;
	private final AlbumMapper albumMapper;

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

	private List<Long> extractCommentIds(List<Comment> comments) {
		return comments.stream().map(Comment::getCommentId).collect(Collectors.toList());
	}

	public PostDetailResponseDto toPostDetailResponseDto(Post post) {
		return PostDetailResponseDto.builder()
			.postId(post.getPostId())
			.postTitle(post.getPostTitle())
			.postContent(post.getPostContent())
			.userId(post.getUser().getUserId())
			.albumId(post.getAlbum().getAlbumId())
			.commentIds(this.extractCommentIds(post.getComments()))
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

		UserSimpleResponseDto postOwner = createUserSimpleResponseDto(post.getUser());
		AlbumReadResponseDto album = albumMapper.toAlbumReadResponseDto(post.getAlbum());
		List<UserSimpleResponseDto> postLikeUsers = createPostLikeUsers(post.getPostLikes());

		return PostMainResponseDto.builder()
			.postId(post.getPostId())
			.postTitle(post.getPostTitle())
			.postContent(post.getPostContent())
			.postOwner(postOwner)
			.createdAt(post.getCreatedAt())
			.updatedAt(post.getUpdatedAt())
			.album(album)
			.postLikeUsers(postLikeUsers)
			.numberOfPostLikes((long)postLikeUsers.size())
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

	private AlbumSimpleResponse createAlbumSimpleResponse(Album album) {
		List<Location> locations = album.getLocations();
		List<LocationReadResponseDto> locationReadResponses = locationMapper.toLocationReadResponseDtoList(locations);

		return AlbumSimpleResponse.builder()
			.photoUrls(album.getPhotoUrls())
			.locations(locationReadResponses)
			.build();
	}

	private List<UserSimpleResponseDto> createPostLikeUsers(List<PostLike> postLikes) {
		List<UserSimpleResponseDto> postLikeUsers = new ArrayList<>();
		for (PostLike postLike : postLikes) {
			User postLikeUser = postLike.getUser();
			postLikeUsers.add(createUserSimpleResponseDto(postLikeUser));
		}
		return postLikeUsers;
	}

}
