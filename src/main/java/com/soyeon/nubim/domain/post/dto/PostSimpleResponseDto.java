package com.soyeon.nubim.domain.post.dto;

import java.time.LocalDateTime;

import com.soyeon.nubim.domain.user.dto.UserSimpleResponseDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PostSimpleResponseDto {
	private Long postId;
	private String postTitle;
	private String postContent;
	private Long numberOfComments;
	private UserSimpleResponseDto user;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	// TODO : Album 구현 완료되면 적절하게 변경되어야 함.
	private Long albumId;
}
