package com.soyeon.nubim.domain.post.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PostCreateResponseDto {
	private Long postId;
	private Long userId;
	private Long albumId;
	private String postTitle;
	private String postContent;
}
