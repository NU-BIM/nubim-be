package com.soyeon.nubim.domain.comment;

import com.soyeon.nubim.domain.comment.dto.CommentCreateRequestDto;
import com.soyeon.nubim.domain.comment.dto.CommentCreateResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@AllArgsConstructor
@RestController
@RequestMapping("/v1/comments")
public class CommentControllerV1 {
    CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentCreateResponseDto> createComment(@RequestBody CommentCreateRequestDto commentCreateRequestDto) {
        CommentCreateResponseDto commentCreateResponseDto = commentService.createComment(commentCreateRequestDto);

        return ResponseEntity
                .created(URI.create("")) // TODO : 조회 api 로 연결
                .body(commentCreateResponseDto);
    }
}
