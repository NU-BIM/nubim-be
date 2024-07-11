package com.soyeon.nubim.domain.post;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;

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
