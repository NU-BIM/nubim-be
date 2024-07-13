package com.soyeon.nubim.domain.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.soyeon.nubim.domain.post.dto.PostCreateRequestDto;
import com.soyeon.nubim.domain.post.dto.PostCreateResponseDto;
import com.soyeon.nubim.domain.post.dto.PostDetailResponseDto;
import com.soyeon.nubim.domain.post.dto.PostSimpleResponseDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PostService {
	private final PostRepository postRepository;
	private final PostMapper postMapper;

	public PostDetailResponseDto findPostDetailById(Long id) {
		Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(id));

		return postMapper.toPostDetailResponseDto(post);
	}

	public PostSimpleResponseDto findPostSimpleById(Long id) {
		Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(id));

		return postMapper.toPostSimpleResponseDto(post);
	}

	public Page<PostSimpleResponseDto> findAllPostsByUserIdOrderByCreatedAt(Long userId, Pageable pageable) {
		Page<Post> postList = postRepository.findByUserUserId(userId, pageable);
		return postList
			.map(postMapper::toPostSimpleResponseDto);
	}

	public PostCreateResponseDto createPost(PostCreateRequestDto postCreateRequestDto) {
		Post post = postMapper.toEntity(postCreateRequestDto);
		postRepository.save(post); // TODO : 글자 수 제한 예외처리
		return postMapper.toPostCreateResponseDto(post);
	}

	public void deleteById(Long id) {
		if (!postRepository.existsById(id)) {
			throw new PostNotFoundException(id);
		}
		postRepository.deleteById(id);
	}

	public Post findPostByIdOrThrow(Long id) {

		return postRepository
			.findById(id)
			.orElseThrow(() -> new PostNotFoundException(id));
	}

	public void validatePostExist(Long postId) {
		if (!postRepository.existsById(postId)) {
			throw new PostNotFoundException(postId);
		}
	}
}
