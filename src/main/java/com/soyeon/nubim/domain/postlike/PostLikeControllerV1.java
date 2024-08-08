package com.soyeon.nubim.domain.postlike;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soyeon.nubim.domain.postlike.dto.PostLikeCreateResponse;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/postlikes")
@RequiredArgsConstructor
public class PostLikeControllerV1 {

	private final PostLikeService postLikeService;

	@Operation(description = "게시글 좋아요 및 좋아요 취소. 게시글에 좋아요를 누르고, 이미 좋아요가 되어있을 시 취소한다.")
	@PostMapping("/{postId}")
	public ResponseEntity<PostLikeCreateResponse> togglePostLike(@PathVariable("postId") Long postId) {
		PostLikeCreateResponse postLike = postLikeService.togglePostLike(postId);

		return ResponseEntity.ok().body(postLike);
	}
}
