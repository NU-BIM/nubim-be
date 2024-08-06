package com.soyeon.nubim.domain.comment;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.soyeon.nubim.common.exception_handler.InvalidQueryParameterException;
import com.soyeon.nubim.domain.comment.dto.CommentCreateRequestDto;
import com.soyeon.nubim.domain.comment.dto.CommentCreateResponseDto;
import com.soyeon.nubim.domain.comment.dto.CommentResponseDto;
import com.soyeon.nubim.domain.post.PostService;
import com.soyeon.nubim.domain.user.User;
import com.soyeon.nubim.domain.user.UserService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/v1/comments")
public class CommentControllerV1 {
	CommentService commentService;
	PostService postService;
	UserService userService;

	@PostMapping
	public ResponseEntity<CommentCreateResponseDto> createComment(
		@Valid @RequestBody CommentCreateRequestDto commentCreateRequestDto) {
		User authorUser = userService.getCurrentUser();
		CommentCreateResponseDto commentCreateResponseDto = commentService.createComment(commentCreateRequestDto,
			authorUser);

		return ResponseEntity
			.created(URI.create("/v1/comments/post/" + commentCreateResponseDto.getPostId()))
			.body(commentCreateResponseDto);
	}

	@Operation(description = "postId 기반으로 댓글 10개씩 조회, sort : 생성시간 오름차순(기본값): asc, 생성시간 내림차순: desc")
	@GetMapping("/post/{postId}")
	public ResponseEntity<Page<CommentResponseDto>> getCommentsByPostId(
		@PathVariable Long postId,
		@RequestParam(defaultValue = "0") Long page,
		@RequestParam(defaultValue = "asc") String sort) {
		postService.validatePostExist(postId);

		Pageable pageable;
		final int DEFAULT_PAGE_SIZE = 10;

		if (sort.equalsIgnoreCase("asc")) {
			pageable = PageRequest.of(page.intValue(), DEFAULT_PAGE_SIZE, Sort.by(Sort.Direction.ASC, "createdAt"));
		} else if (sort.equalsIgnoreCase("desc")) {
			pageable = PageRequest.of(page.intValue(), DEFAULT_PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));
		} else {
			throw new InvalidQueryParameterException("sort");
		}
		return ResponseEntity.ok(commentService.findCommentsByPostIdAndPageable(postId, pageable));
	}

}
