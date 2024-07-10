package com.soyeon.nubim.domain.post;

import com.soyeon.nubim.domain.post.dto.PostCreateRequestDto;
import com.soyeon.nubim.domain.post.dto.PostCreateResponseDto;
import com.soyeon.nubim.domain.post.dto.PostDetailResponseDto;
import com.soyeon.nubim.domain.post.dto.PostSimpleResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<PostSimpleResponseDto> findAllPostsByUserIdOrderByCreatedAtDesc(Long userId) {
        List<Post> postList = postRepository.findByUserUserIdOrderByCreatedAtDesc(userId);
        return postList.stream()
                .map(post -> postMapper.toPostSimpleResponseDto(post))
                .toList();
    }

    public List<PostSimpleResponseDto> findAllPostsByUserIdOrderByCreatedAtAsc(Long userId) {
        List<Post> postList = postRepository.findByUserUserIdOrderByCreatedAtAsc(userId);
        return postList.stream()
                .map(post -> postMapper.toPostSimpleResponseDto(post))
                .toList();
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
}
