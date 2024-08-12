package com.soyeon.nubim.domain.post_bookmark;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.soyeon.nubim.common.exception_handler.InvalidQueryParameterException;
import com.soyeon.nubim.domain.post.dto.PostSimpleResponseDto;
import com.soyeon.nubim.domain.post_bookmark.dto.PostBookmarkRequestDto;
import com.soyeon.nubim.domain.post_bookmark.dto.PostBookmarkResponseDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/post-bookmarks")
public class PostBookmarkControllerV1 {

	private final PostBookmarkService postBookmarkService;

	@PostMapping
	public ResponseEntity<PostBookmarkResponseDto> bookmarkPost(@RequestBody PostBookmarkRequestDto requestDto) {
		PostBookmarkResponseDto responseDto = postBookmarkService.bookmarkPost(requestDto);
		return ResponseEntity
			.created(URI.create("")) // TODO : 북마크 조회 연결
			.body(responseDto);
	}

	@DeleteMapping
	public ResponseEntity<PostBookmarkResponseDto> deleteBookmarkPost(@RequestBody PostBookmarkRequestDto requestDto) {
		PostBookmarkResponseDto responseDto = postBookmarkService.deleteBookmarkPost(requestDto);
		return ResponseEntity
			.ok()
			.body(responseDto);
	}

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