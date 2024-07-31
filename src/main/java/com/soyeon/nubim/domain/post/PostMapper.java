package com.soyeon.nubim.domain.post;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.soyeon.nubim.domain.album.Album;
import com.soyeon.nubim.domain.comment.Comment;
import com.soyeon.nubim.domain.comment.dto.CommentResponseDto;
import com.soyeon.nubim.domain.post.dto.PostCreateRequestDto;
import com.soyeon.nubim.domain.post.dto.PostCreateResponseDto;
import com.soyeon.nubim.domain.post.dto.PostDetailResponseDto;
import com.soyeon.nubim.domain.post.dto.PostMainResponseDto;
import com.soyeon.nubim.domain.post.dto.PostSimpleResponseDto;
import com.soyeon.nubim.domain.user.User;
import com.soyeon.nubim.domain.user.dto.UserSimpleResponseDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PostMapper {

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
			.userId(post.getUser().getUserId())
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

	public PostMainResponseDto toPostMainResponseDto(Post post, CommentResponseDto representativeComment) {
		UserSimpleResponseDto userSimpleResponseDto = UserSimpleResponseDto.builder()
			.userId(post.getUser().getUserId())
			.profileImageUrl(post.getUser().getProfileImageUrl())
			.nickname(post.getUser().getNickname())
			.build();

		return PostMainResponseDto.builder()
			.postId(post.getPostId())
			.postTitle(post.getPostTitle())
			.postContent(post.getPostContent())
			.numberOfComments((long)post.getComments().size())
			.representativeComment(representativeComment)
			.user(userSimpleResponseDto)
			.albumId(post.getAlbum().getAlbumId())
			.createdAt(post.getCreatedAt())
			.updatedAt(post.getUpdatedAt())
			.build();
	}

	public List<PostDetailResponseDto> toPostDetailResponseDtos(List<Post> posts) {
		List<PostDetailResponseDto> postDetailResponseDtos = new ArrayList<>(posts.size());
		for (Post post : posts) {
			postDetailResponseDtos.add(toPostDetailResponseDto(post));
		}
		return postDetailResponseDtos;
	}
}
