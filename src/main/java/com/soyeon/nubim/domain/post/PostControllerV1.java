package com.soyeon.nubim.domain.post;

import com.soyeon.nubim.domain.post.dto.PostCreateRequestDto;
import com.soyeon.nubim.domain.post.dto.PostCreateResponseDto;
import com.soyeon.nubim.domain.post.dto.PostSimpleResponseDto;
import com.soyeon.nubim.domain.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/v1/posts")
public class PostControllerV1 {

    private PostService postService;
    private UserService userService;

    @PostMapping
    public ResponseEntity<PostCreateResponseDto> createPost(@RequestBody PostCreateRequestDto postCreateRequestDto) {
        PostCreateResponseDto postCreateResponseDto = postService.createPost(postCreateRequestDto);

        return ResponseEntity
                .created(URI.create(String.format("/v1/posts/%d", postCreateResponseDto.getPostId())))
                .body(postCreateResponseDto);
    }

    @Operation(description = "type이 비어있을 경우: 자세한 게시글 type=simple: 미리보기")
    @GetMapping("/{postId}")
    public ResponseEntity<?> getPostDetail(
            @PathVariable Long postId,
            @RequestParam(required = false) String type) {
        if (type == null) {
            return ResponseEntity.ok(postService.findPostDetailById(postId));
        } else if (type.equals("simple")) {
            return ResponseEntity.ok(postService.findPostSimpleById(postId));
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(description = "userId를 기준으로 게시글 미리보기 리스트 시간순 정렬 응답, 기본은 내림차순, orderBy=asc일경우 오름차순")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostSimpleResponseDto>> getPostsByUserId(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "desc") String sort) {
        userService.validateUserExists(userId);
        if (sort.equals("desc")) {
            return ResponseEntity.ok(postService.findAllPostsByUserIdOrderByCreatedAtDesc(userId));
        } else if (sort.equals("asc")) {
            return ResponseEntity.ok(postService.findAllPostsByUserIdOrderByCreatedAtAsc(userId));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }


    // TODO : 배포 전 soft delete로 변경 필요
    @DeleteMapping("{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        postService.deleteById(postId);
        return ResponseEntity.ok().build();
    }

}