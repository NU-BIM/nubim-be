package com.soyeon.nubim.domain.post.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PostCreateRequestDto {
	private Long userId; // TODO : Delete after test
	private Long albumId;
	private String postTitle;
	private String postContent;
}
