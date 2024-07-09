package com.soyeon.nubim.domain.comment;

import com.soyeon.nubim.domain.comment.dto.CommentCreateRequestDto;
import com.soyeon.nubim.domain.comment.dto.CommentCreateResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class CommentService {
    CommentRepository commentRepository;
    CommentMapper commentMapper;

    public CommentCreateResponseDto createComment(CommentCreateRequestDto commentCreateRequestDto) {
        Comment comment = commentMapper.toEntity(commentCreateRequestDto);
        commentRepository.save(comment); // TODO : 글자 수 검증 필요

        return commentMapper.toCommentCreateResponseDto(comment);
    }
}
