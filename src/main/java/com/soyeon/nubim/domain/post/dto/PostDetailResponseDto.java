package com.soyeon.nubim.domain.post.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PostDetailResponseDto {
	private Long postId;
	private String postTitle;
	private String postContent;

	// TODO : User, Album, Comment 구현 완료되면 적절하게 변경되어야 함.
	private Long userId;
	private Long albumId;
	private List<Long> commentIds;
}
