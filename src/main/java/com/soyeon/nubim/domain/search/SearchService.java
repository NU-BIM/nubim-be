package com.soyeon.nubim.domain.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.soyeon.nubim.domain.post.PostService;
import com.soyeon.nubim.domain.post.dto.PostMainResponseDto;
import com.soyeon.nubim.domain.user.LoggedInUserService;
import com.soyeon.nubim.domain.user.User;
import com.soyeon.nubim.domain.user.UserMapper;
import com.soyeon.nubim.domain.user.UserService;
import com.soyeon.nubim.domain.user.dto.UserSimpleWithIsFollowedResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchService {
	private final UserService userService;
	private final PostService postService;
	private final UserMapper userMapper;
	private final LoggedInUserService loggedInUserService;

	public Page<UserSimpleWithIsFollowedResponseDto> searchUsers(String query, Pageable pageable) {
		Page<User> users = userService.searchUserByNickname(pageable, query);
		return users.map(user -> {
			UserSimpleWithIsFollowedResponseDto userResponseDto = userMapper.toUserSimpleWithIsFollowedResponseDto(
				user);
			if (user.getUserId() == loggedInUserService.getCurrentUser().getUserId()) {
				userResponseDto.setIsFollowed(null);
			}
			return userResponseDto;
		});
	}

	public Page<PostMainResponseDto> searchPosts(String query, Pageable pageable) {
		return postService.searchPostsByTitleOrContent(pageable, query);
	}
}
