package com.soyeon.nubim.domain.post_bookmark.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PostBookmarkResponseDto {
	private Long postId;
	private String message;
	private Boolean bookmarkResult;
}
