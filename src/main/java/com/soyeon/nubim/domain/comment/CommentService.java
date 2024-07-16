package com.soyeon.nubim.domain.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.soyeon.nubim.domain.comment.dto.CommentCreateRequestDto;
import com.soyeon.nubim.domain.comment.dto.CommentCreateResponseDto;
import com.soyeon.nubim.domain.comment.dto.CommentResponseDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommentService {
	private final CommentRepository commentRepository;
	private final CommentMapper commentMapper;

	public CommentCreateResponseDto createComment(CommentCreateRequestDto commentCreateRequestDto) {
		Comment comment = commentMapper.toEntity(commentCreateRequestDto);
		commentRepository.save(comment); // TODO : 글자 수 검증 필요

		return commentMapper.toCommentCreateResponseDto(comment);
	}

	public Page<CommentResponseDto> findCommentsByPostIdAndPageable(Long postId, Pageable pageable) {
		Page<Comment> commentList = commentRepository.findByPostPostId(postId, pageable);

		return commentList.map(commentMapper::toCommentResponseDto);
	}
}
