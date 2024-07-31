package com.soyeon.nubim.domain.comment.dto;

import com.soyeon.nubim.domain.user.dto.UserSimpleResponseDto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CommentResponseDto {
	private Long commentId;
	private UserSimpleResponseDto user;
	private Long postId;
	private Long parentCommentId;
	private String content;
}
