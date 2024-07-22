package com.soyeon.nubim.domain.post;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.soyeon.nubim.domain.album.Album;
import com.soyeon.nubim.domain.album.AlbumNotFoundException;
import com.soyeon.nubim.domain.album.AlbumService;
import com.soyeon.nubim.domain.comment.Comment;
import com.soyeon.nubim.domain.post.dto.PostCreateRequestDto;
import com.soyeon.nubim.domain.post.dto.PostCreateResponseDto;
import com.soyeon.nubim.domain.post.dto.PostDetailResponseDto;
import com.soyeon.nubim.domain.post.dto.PostSimpleResponseDto;
import com.soyeon.nubim.domain.user.User;
import com.soyeon.nubim.domain.user.UserService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PostMapper {
	private final UserService userService;
	private final AlbumService albumService;

	public Post toEntity(PostCreateRequestDto postCreateRequestDto, User authorUser) {
		Album linkedAlbum = albumService
			.findById(postCreateRequestDto.getAlbumId())
			.orElseThrow(() -> new AlbumNotFoundException(postCreateRequestDto.getAlbumId()));

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
		return PostSimpleResponseDto.builder()
			.postId(post.getPostId())
			.postTitle(post.getPostTitle())
			.postContent(post.getPostContent())
			.numberOfComments((long)post.getComments().size())
			.userId(post.getUser().getUserId())
			.albumId(post.getAlbum().getAlbumId())
			.build();
	}
}
