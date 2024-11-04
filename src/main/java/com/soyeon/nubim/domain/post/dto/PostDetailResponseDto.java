package com.soyeon.nubim.domain.post.dto;

import com.soyeon.nubim.domain.album.dto.AlbumResponseDto;
import com.soyeon.nubim.domain.user.dto.UserResponseDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PostDetailResponseDto implements PostResponseDto {
	private Long postId;
	private String postTitle;
	private String postContent;
	private AlbumResponseDto album;
	private UserResponseDto user;
	private Boolean isReported;
}
