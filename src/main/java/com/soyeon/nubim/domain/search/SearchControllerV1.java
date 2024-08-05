package com.soyeon.nubim.domain.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/search")
@RequiredArgsConstructor
public class SearchControllerV1 {
	private final SearchService searchService;

	private static final int DEFAULT_PAGE_SIZE = 20;

	@GetMapping
	public ResponseEntity<Page<?>> searchWithType(
		@RequestParam(defaultValue = "nickname") @Parameter(description = "[ nickname, post ]") String type,
		@RequestParam(required = true) @Size(min = 2) String query,
		@RequestParam(defaultValue = "0") Integer page
	) {
		if (type.equals("nickname")) {
			Pageable pageable = PageRequest.of(page, DEFAULT_PAGE_SIZE, Sort.by(Sort.Direction.ASC, "nickname"));
			return ResponseEntity.ok().body(searchService.searchUsers(query, pageable));
		} else if (type.equals("post")) {
			Pageable pageable = PageRequest.of(page, DEFAULT_PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));
			return ResponseEntity.ok().body(searchService.searchPosts(query, pageable));
		} else {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
	}

}
