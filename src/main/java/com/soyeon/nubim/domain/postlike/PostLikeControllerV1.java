package com.soyeon.nubim.domain.postlike;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soyeon.nubim.domain.postlike.dto.PostLikeResponse;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/postlikes")
@RequiredArgsConstructor
public class PostLikeControllerV1 {

	private final PostLikeService postLikeService;

	@Operation(description = "게시글 좋아요 생성")
	@PostMapping("/{postId}")
	public ResponseEntity<PostLikeResponse> likePost(@PathVariable("postId") Long postId) {
		PostLikeResponse postLike = postLikeService.createPostLike(postId);

		return ResponseEntity.ok().body(postLike);
	}
}
