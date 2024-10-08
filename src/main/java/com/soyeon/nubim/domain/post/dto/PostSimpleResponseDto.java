package com.soyeon.nubim.domain.post.dto;

import java.time.LocalDateTime;

import com.soyeon.nubim.domain.album.dto.AlbumResponseDto;
import com.soyeon.nubim.domain.user.dto.UserSimpleResponseDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PostSimpleResponseDto implements PostResponseDto {
	private Long postId;
	private String postTitle;
	private String postContent;
	private Long numberOfComments;
	private UserSimpleResponseDto user;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
	
	private AlbumResponseDto album;
}
