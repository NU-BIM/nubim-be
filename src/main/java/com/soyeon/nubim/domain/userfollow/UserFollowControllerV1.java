package com.soyeon.nubim.domain.userfollow;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.soyeon.nubim.domain.user.dto.UserSimpleResponseDto;
import com.soyeon.nubim.domain.userfollow.dto.FollowUserResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1")
public class UserFollowControllerV1 {
	final UserFollowService userFollowService;

	@Operation(description = "로그인된 유저가 해당 nickname 팔로우")
	@PostMapping("/follows/{nickname}")
	public ResponseEntity<FollowUserResponseDto> followUser(@PathVariable String nickname) {
		FollowUserResponseDto followUserResponseDto = userFollowService.createFollow(nickname);

		return ResponseEntity
			.created(URI.create("")) // TODO : 팔로잉 조회 uri 추가
			.body(followUserResponseDto);
	}

	@DeleteMapping("/follows/{nickname}")
	public ResponseEntity<FollowUserResponseDto> unfollowUser(@PathVariable String nickname) {
		FollowUserResponseDto followUserResponseDto = userFollowService.deleteUserFollow(nickname);

		return ResponseEntity
			.ok()
			.body(followUserResponseDto);
	}

	@GetMapping("/followers")
	public ResponseEntity<Page<UserSimpleResponseDto>> getFollowers(
		@RequestParam(defaultValue = "0") Long page,
		@RequestParam(defaultValue = "desc") String sort,
		@RequestParam(defaultValue = "20") Long pageSize) {

		Pageable pageable;
		if (sort.equalsIgnoreCase("asc")) {
			pageable = PageRequest.of(page.intValue(), pageSize.intValue(), Sort.by(Sort.Direction.ASC, "createdAt"));
		} else if (sort.equalsIgnoreCase("desc")) {
			pageable = PageRequest.of(page.intValue(), pageSize.intValue(), Sort.by(Sort.Direction.DESC, "createdAt"));
		} else {
			return ResponseEntity.badRequest().build();
		}
		return ResponseEntity.ok(userFollowService.getFollowers(pageable));
	}

	@GetMapping("/followees")
	public ResponseEntity<Page<UserSimpleResponseDto>> getFollowees(
		@RequestParam(defaultValue = "0") Long page,
		@RequestParam(defaultValue = "desc") String sort,
		@RequestParam(defaultValue = "20") Long pageSize) {

		Pageable pageable;
		try {
			pageable = determinePageable(page, sort, pageSize);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().build();
		}

		return ResponseEntity.ok(userFollowService.getFollowees(pageable));
	}

	private Pageable determinePageable(Long page, String sort, Long pageSize) {
		Pageable pageable;
		if (sort.equalsIgnoreCase("asc")) {
			pageable = PageRequest.of(page.intValue(), pageSize.intValue(), Sort.by(Sort.Direction.ASC, "createdAt"));
		} else if (sort.equalsIgnoreCase("desc")) {
			pageable = PageRequest.of(page.intValue(), pageSize.intValue(), Sort.by(Sort.Direction.DESC, "createdAt"));
		} else {
			throw new IllegalArgumentException();
		}
		return pageable;
	}
}