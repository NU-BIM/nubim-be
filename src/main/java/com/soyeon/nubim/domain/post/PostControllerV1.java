package com.soyeon.nubim.domain.post;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soyeon.nubim.domain.post.dto.PostCreateRequestDto;
import com.soyeon.nubim.domain.post.dto.PostCreateResponseDto;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/v1/posts")
public class PostControllerV1 {

	private PostService postService;
	private PostMapper postMapper;
	
	@PostMapping
	public ResponseEntity<PostCreateResponseDto> createPost(@RequestBody PostCreateRequestDto postCreateRequestDto) {
		Post postRequest = postMapper.toEntity(postCreateRequestDto);
		Post savedPost = postService.createPost(postRequest);

		return ResponseEntity
			.created(URI.create(String.format("/v1/posts/%d", savedPost.getPostId())))
			.body(postMapper.toPostCreateResponseDto(savedPost));
	}

}