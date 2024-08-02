package com.soyeon.nubim.domain.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.soyeon.nubim.domain.post.PostService;
import com.soyeon.nubim.domain.user.UserService;
import com.soyeon.nubim.domain.user.dto.UserSimpleResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchService {
	private final UserService userService;
	private final PostService postService;

	public Page<UserSimpleResponseDto> searchUsers(String query, Pageable pageable) {
		return userService.searchUserByNickname(pageable, query);
	}

	// public Page<PostSimpleResponseDto> searchPosts(String query, Pageable pageable) {
	//
	// }
}
