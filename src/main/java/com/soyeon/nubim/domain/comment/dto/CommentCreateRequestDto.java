package com.soyeon.nubim.domain.comment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CommentCreateRequestDto {
	private Long userId;
	private Long postId;
	private Long parentCommentId;
	private String content;
}
