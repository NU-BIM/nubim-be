package com.soyeon.nubim.domain.post;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class PostService {
    private PostRepository postRepository;

    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
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
