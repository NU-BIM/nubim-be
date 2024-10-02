package com.soyeon.nubim.domain.post.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.soyeon.nubim.domain.album.dto.AlbumResponseDto;
import com.soyeon.nubim.domain.comment.dto.CommentResponseDto;
import com.soyeon.nubim.domain.user.dto.UserResponseDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PostMainResponseDto implements PostResponseDto {
	private Long postId;
	private String postTitle;
	private String postContent;
	private UserResponseDto user;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	private AlbumResponseDto album;

	private List<UserResponseDto> postLikeUsers;
	private Long numberOfPostLikes;

	private CommentResponseDto representativeComment;
	private Long numberOfComments;

}