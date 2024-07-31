package com.soyeon.nubim.domain.user.dto;

import java.util.List;

import com.soyeon.nubim.domain.album.dto.AlbumReadResponseDto;
import com.soyeon.nubim.domain.post.dto.PostDetailResponseDto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserProfileResponseDto {
	private Long userId;
	private String username;
	private String nickname;
	private String profileImageUrl;
	private String profileIntroduction;
	private List<AlbumReadResponseDto> albums;
	private List<PostDetailResponseDto> posts;
	private List<UserFollowResponseDto> followers;
	private List<UserFollowResponseDto> followees;
	private Long followerCount;
	private Long followeeCount;

}