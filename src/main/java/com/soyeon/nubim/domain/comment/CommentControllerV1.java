package com.soyeon.nubim.domain.comment;

import com.soyeon.nubim.domain.comment.dto.CommentCreateRequestDto;
import com.soyeon.nubim.domain.comment.dto.CommentCreateResponseDto;
import com.soyeon.nubim.domain.comment.dto.CommentResponseDto;
import com.soyeon.nubim.domain.post.PostService;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@AllArgsConstructor
@RestController
@RequestMapping("/v1/comments")
public class CommentControllerV1 {
    CommentService commentService;
    PostService postService;

    @PostMapping
    public ResponseEntity<CommentCreateResponseDto> createComment(@RequestBody CommentCreateRequestDto commentCreateRequestDto) {
        CommentCreateResponseDto commentCreateResponseDto = commentService.createComment(commentCreateRequestDto);

        return ResponseEntity
                .created(URI.create("")) // TODO : 조회 api 로 연결
                .body(commentCreateResponseDto);
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<Page<CommentResponseDto>> getCommentsByPostId(
            @PathVariable
            Long postId,
            @PageableDefault(size = 10, page = 0, sort = "createdAt", direction = Sort.Direction.ASC)
            @ParameterObject
            Pageable pageable) {
        postService.validatePostExist(postId);

        return ResponseEntity.ok(commentService.findCommentsByPostIdAndPageable(postId, pageable));
    }

}
