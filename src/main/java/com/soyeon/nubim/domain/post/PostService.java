package com.soyeon.nubim.domain.post;

import java.util.Optional;

import com.soyeon.nubim.domain.post.dto.PostCreateRequestDto;
import com.soyeon.nubim.domain.post.dto.PostCreateResponseDto;
import com.soyeon.nubim.domain.post.dto.PostDetailResponseDto;
import com.soyeon.nubim.domain.post.dto.PostSimpleResponseDto;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class PostService {
	private PostRepository postRepository;
	private PostMapper postMapper;

	public PostDetailResponseDto findPostDetailById(Long id) {
		Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(id));

		return postMapper.toPostDetailResponseDto(post);
	}

	public PostSimpleResponseDto findPostSimpleById(Long id) {
		Post post = postRepository.findById(id).orElseThrow(() -> new PostNotFoundException(id));

		return postMapper.toPostSimpleResponseDto(post);
	}

	public PostCreateResponseDto createPost(PostCreateRequestDto postCreateRequestDto) {
		Post post = postMapper.toEntity(postCreateRequestDto);
		postRepository.save(post);
		return postMapper.toPostCreateResponseDto(post);
	}

	public void deleteById(Long id) {
		if (!postRepository.existsById(id)) {
			throw new PostNotFoundException(id);
		}
		postRepository.deleteById(id);
	}
}
