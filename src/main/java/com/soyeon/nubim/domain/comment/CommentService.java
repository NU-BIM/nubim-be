package com.soyeon.nubim.domain.comment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.soyeon.nubim.domain.comment.dto.CommentCreateRequestDto;
import com.soyeon.nubim.domain.comment.dto.CommentCreateResponseDto;
import com.soyeon.nubim.domain.comment.dto.CommentResponseDto;
import com.soyeon.nubim.domain.post.Post;
import com.soyeon.nubim.domain.post.PostService;
import com.soyeon.nubim.domain.user.User;
import com.soyeon.nubim.domain.user.UserMapper;
import com.soyeon.nubim.domain.user.dto.UserSimpleResponseDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommentService {
	private final CommentRepository commentRepository;
	private final PostService postService;
	private final CommentMapper commentMapper;
	private final UserMapper userMapper;

	public CommentCreateResponseDto createComment(CommentCreateRequestDto commentCreateRequestDto, User user) {
		Post post = postService.findPostByIdOrThrow(commentCreateRequestDto.getPostId());
		Comment comment = commentMapper.toEntity(commentCreateRequestDto, user, post);
		commentRepository.save(comment); // TODO : 글자 수 검증 필요

		return commentMapper.toCommentCreateResponseDto(comment);
	}

	public Page<CommentResponseDto> findCommentsByPostIdAndPageable(Long postId, Pageable pageable) {
		Page<Comment> commentList = commentRepository.findByPostPostId(postId, pageable);

		return commentList.map(comment -> {
			UserSimpleResponseDto userSimpleResponseDto = userMapper.toUserSimpleResponseDto(comment.getUser());
			return commentMapper.toCommentResponseDto(comment, userSimpleResponseDto);
		});
	}
}
