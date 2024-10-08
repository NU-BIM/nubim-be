package com.soyeon.nubim.domain.user;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.soyeon.nubim.domain.album.Album;
import com.soyeon.nubim.domain.album.dto.AlbumReadResponseDto;
import com.soyeon.nubim.domain.album.mapper.AlbumMapper;
import com.soyeon.nubim.domain.post.Post;
import com.soyeon.nubim.domain.post.PostMapper;
import com.soyeon.nubim.domain.post.dto.PostDetailResponseDto;
import com.soyeon.nubim.domain.user.dto.UserFollowResponseDto;
import com.soyeon.nubim.domain.user.dto.UserProfileResponseDto;
import com.soyeon.nubim.domain.user.dto.UserSimpleResponseDto;
import com.soyeon.nubim.domain.user.dto.UserSimpleWithIsFollowedResponseDto;
import com.soyeon.nubim.domain.userfollow.UserFollow;
import com.soyeon.nubim.domain.userfollow.UserFollowRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserMapper {

	private final AlbumMapper albumMapper;
	private final PostMapper postMapper;
	private final UserFollowRepository userFollowRepository;
	private final LoggedInUserService loggedInUserService;

	public UserSimpleResponseDto toUserSimpleResponseDto(User user) {
		return UserSimpleResponseDto.builder()
			.username(user.getUsername())
			.nickname(user.getNickname())
			.profileImageUrl(user.getProfileImageUrl())
			.build();
	}

	public UserSimpleWithIsFollowedResponseDto toUserSimpleWithIsFollowedResponseDto(User user) {
		User currentUser = loggedInUserService.getCurrentUser();
		Boolean isFollowed = userFollowRepository.existsByFollowerAndFollowee(currentUser, user);

		return UserSimpleWithIsFollowedResponseDto.builder()
			.username(user.getUsername())
			.nickname(user.getNickname())
			.profileImageUrl(user.getProfileImageUrl())
			.isFollowed(isFollowed)
			.build();
	}

	public UserProfileResponseDto toUserProfileResponseDto(User user) {
		List<Album> albums = user.getAlbums();
		List<AlbumReadResponseDto> albumReadResponseDtos = albumMapper.toAlbumReadResponseDtoList(albums);

		List<Post> posts = user.getPosts();
		List<PostDetailResponseDto> postDetailResponseDtos = postMapper.toPostDetailResponseDtos(posts);

		List<UserFollowResponseDto> followers = getCurrentUserFollowers(user);
		List<UserFollowResponseDto> followees = getCurrentUserFollowees(user);
		markFollowedFollowers(followers, followees);

		return UserProfileResponseDto.builder()
			.userId(user.getUserId())
			.username(user.getUsername())
			.nickname(user.getNickname())
			.profileImageUrl(user.getProfileImageUrl())
			.profileIntroduction(user.getProfileIntroduction())
			.albums(albumReadResponseDtos)
			.posts(postDetailResponseDtos)
			.followers(followers)
			.followees(followees)
			.followerCount((long)followers.size())
			.followeeCount((long)followees.size())
			.build();
	}

	//사용자가 팔로우하고 있는 사용자의 팔로워의 isFollowing 값을 true 로 변경
	private void markFollowedFollowers(List<UserFollowResponseDto> followers, List<UserFollowResponseDto> followees) {
		for (UserFollowResponseDto follower : followers) {
			if (followees.contains(follower)) {
				follower.setIsFollowingTrue();
			}
		}
	}

	private List<UserFollowResponseDto> getCurrentUserFollowers(User currentUser) {
		List<UserFollowResponseDto> followerResponseDtos = new ArrayList<>();
		for (UserFollow follower : currentUser.getFollowers()) {
			User findFollower = follower.getFollower();
			followerResponseDtos.add(toUserFollowResponseDto(findFollower, false));
		}
		return followerResponseDtos;
	}

	private List<UserFollowResponseDto> getCurrentUserFollowees(User currentUser) {
		List<UserFollowResponseDto> followeeResponseDtos = new ArrayList<>();
		for (UserFollow followee : currentUser.getFollowees()) {
			User findFollowee = followee.getFollowee();
			followeeResponseDtos.add(toUserFollowResponseDto(findFollowee, true));
		}
		return followeeResponseDtos;
	}

	private UserFollowResponseDto toUserFollowResponseDto(User user, boolean isFollowing) {
		return UserFollowResponseDto.builder()
			.username(user.getUsername())
			.nickname(user.getNickname())
			.profileImageUrl(user.getProfileImageUrl())
			.isFollowing(isFollowing)
			.build();
	}
}
