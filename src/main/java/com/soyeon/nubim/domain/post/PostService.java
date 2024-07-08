package com.soyeon.nubim.domain.post;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class PostService {
	private PostRepository postRepository;

	public Optional<Post> findById(Long id) {
		return postRepository.findById(id);
	}

	public Post createPost(Post post) {
		return postRepository.save(post);
	}

	public void deleteById(Long id) {
		if (!postRepository.existsById(id)) {
			throw new PostNotFoundException(id);
		}
		postRepository.deleteById(id);
		return;
	}
}
