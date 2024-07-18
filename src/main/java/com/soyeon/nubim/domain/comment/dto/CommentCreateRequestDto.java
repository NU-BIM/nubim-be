package com.soyeon.nubim.domain.comment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CommentCreateRequestDto {
	@NotNull
	private Long postId;

	private Long parentCommentId;

	@NotNull
	@Size(min = 1, max = 2200)
	private String content;
}
