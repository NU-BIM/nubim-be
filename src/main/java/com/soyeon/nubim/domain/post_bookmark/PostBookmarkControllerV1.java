package com.soyeon.nubim.domain.post_bookmark;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.soyeon.nubim.common.exception_handler.InvalidQueryParameterException;
import com.soyeon.nubim.domain.post.dto.PostSimpleResponseDto;
import com.soyeon.nubim.domain.post_bookmark.dto.PostBookmarkResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/post-bookmarks")
public class PostBookmarkControllerV1 {

	private final PostBookmarkService postBookmarkService;

	@Operation(description = "게시글 북마크 및 북마크 취소")
	@PostMapping("/{postId}")
	public ResponseEntity<PostBookmarkResponseDto> bookmarkPost(@PathVariable Long postId) {
		PostBookmarkResponseDto responseDto = postBookmarkService.togglePostBookmark(postId);
		return ResponseEntity
			.ok(responseDto);
	}

	@Operation(description = "사용자가 북마크한 게시글 목록 반환")
	@GetMapping
	public ResponseEntity<Page<PostSimpleResponseDto>> getBookmarks(
		@RequestParam(defaultValue = "0") Long page,
		@RequestParam(defaultValue = "desc") String sort,
		@RequestParam(defaultValue = "20") Long pageSize) {
		Pageable pageable;

		if (sort.equalsIgnoreCase("asc")) {
			pageable = PageRequest.of(page.intValue(), pageSize.intValue(), Sort.by(Sort.Direction.ASC, "createdAt"));
		} else if (sort.equalsIgnoreCase("desc")) {
			pageable = PageRequest.of(page.intValue(), pageSize.intValue(), Sort.by(Sort.Direction.DESC, "createdAt"));
		} else {
			throw new InvalidQueryParameterException("sort");
		}
		Page<PostSimpleResponseDto> userBookmarks = postBookmarkService.getUserBookmarks(pageable);
		return ResponseEntity.ok(userBookmarks);
	}
}