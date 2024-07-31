package com.soyeon.nubim.domain.comment;

import org.springframework.stereotype.Component;

import com.soyeon.nubim.domain.comment.dto.CommentCreateRequestDto;
import com.soyeon.nubim.domain.comment.dto.CommentCreateResponseDto;
import com.soyeon.nubim.domain.comment.dto.CommentResponseDto;
import com.soyeon.nubim.domain.post.Post;
import com.soyeon.nubim.domain.user.User;
import com.soyeon.nubim.domain.user.dto.UserSimpleResponseDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CommentMapper {
	public Comment toEntity(CommentCreateRequestDto commentCreateRequestDto, User user, Post post) {
		return Comment.builder()
			.user(user)
			.post(post)
			.parentComment(null) // TODO : 대댓글 구현 시 수정
			.commentContent(commentCreateRequestDto.getContent())
			.build();
	}

	public CommentCreateResponseDto toCommentCreateResponseDto(Comment comment) {
		return CommentCreateResponseDto.builder()
			.commentId(comment.getCommentId())
			.userId(comment.getUser().getUserId())
			.postId(comment.getPost().getPostId())
			.parentCommentId(null) // TODO : 대댓글 구현 시 수정
			.content(comment.getCommentContent())
			.build();
	}

	public CommentResponseDto toCommentResponseDto(Comment comment, UserSimpleResponseDto userSimpleResponseDto) {
		return CommentResponseDto.builder()
			.commentId(comment.getCommentId())
			.user(userSimpleResponseDto)
			.postId(comment.getPost().getPostId())
			.parentCommentId(null) // TODO : 대댓글 구현 시 수정
			.content(comment.getCommentContent())
			.build();
	}
}
