package com.soyeon.nubim.domain.post.dto;

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

	// TODO : User, Album, Comment 구현 완료되면 적절하게 변경되어야 함.
	private Long userId;
	private Long albumId;
}
