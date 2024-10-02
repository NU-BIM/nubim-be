package com.soyeon.nubim.domain.album;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soyeon.nubim.common.util.aws.S3AndCdnUrlConverter;
import com.soyeon.nubim.common.util.aws.S3ImageDeleter;
import com.soyeon.nubim.domain.album.dto.AlbumCreateRequestDto;
import com.soyeon.nubim.domain.album.dto.AlbumCreateResponseDto;
import com.soyeon.nubim.domain.album.dto.AlbumReadResponseDto;
import com.soyeon.nubim.domain.album.dto.AlbumUpdateRequestDto;
import com.soyeon.nubim.domain.album.dto.LocationUpdateRequestDto;
import com.soyeon.nubim.domain.album.exception.AlbumNotFoundException;
import com.soyeon.nubim.domain.album.mapper.AlbumMapper;
import com.soyeon.nubim.domain.album.mapper.LocationMapper;
import com.soyeon.nubim.domain.post.PostRepository;
import com.soyeon.nubim.domain.user.LoggedInUserService;
import com.soyeon.nubim.domain.user.User;
import com.soyeon.nubim.domain.user.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlbumService {

	private final AlbumValidator albumValidator;
	private final AlbumRepository albumRepository;
	private final AlbumMapper albumMapper;
	private final UserService userService;
	private final LocationMapper locationMapper;
	private final S3ImageDeleter s3ImageDeleter;
	private final PostRepository postRepository;
	private final S3AndCdnUrlConverter s3AndCdnUrlConverter;
	private final LoggedInUserService loggedInUserService;

	public Album findById(Long id) {
		return albumRepository.findById(id)
			.orElseThrow(() -> new AlbumNotFoundException(id));
	}

	@Transactional
	public AlbumCreateResponseDto createAlbum(AlbumCreateRequestDto albumCreateRequestDto) {
		User currentUser = loggedInUserService.getCurrentUser();
		albumValidator.validateAlbumPathCoordinates(albumCreateRequestDto);

		Album album = albumMapper.toEntity(albumCreateRequestDto, currentUser);

		Album savedAlbum = albumRepository.save(album);
		return albumMapper.toAlbumCreateResponseDto(savedAlbum);
	}

	public AlbumReadResponseDto findByIdWithLocations(Long albumId) {
		Album album = albumRepository.findByIdWithLocations(albumId)
			.orElseThrow(() -> new AlbumNotFoundException(albumId));

		return albumMapper.toAlbumReadResponseDto(album);
	}

	public List<AlbumReadResponseDto> findAlbumsByUserNickname(String nickname) {
		User user = userService.getUserByNickname(nickname);

		List<Album> albums = albumRepository.findByUser(user);

		List<AlbumReadResponseDto> albumReadResponseDtos = new ArrayList<>(albums.size());
		for (Album album : albums) {
			albumReadResponseDtos.add(albumMapper.toAlbumReadResponseDto(album));
		}
		return albumReadResponseDtos;
	}

	public List<AlbumReadResponseDto> findAlbumsByCurrentUser(boolean unlinked) {
		String currentUserEmail = loggedInUserService.getCurrentUserEmail();

		List<Album> albums;
		if (unlinked) {
			albums = albumRepository.findUnlinkedAlbumsByEmail(currentUserEmail);
		} else {
			albums = albumRepository.findAlbumsByEmail(currentUserEmail);
		}

		List<AlbumReadResponseDto> albumReadResponseDtos = new ArrayList<>(albums.size());
		for (Album album : albums) {
			albumReadResponseDtos.add(albumMapper.toAlbumReadResponseDto(album));
		}

		return albumReadResponseDtos;
	}

	@Transactional
	public AlbumReadResponseDto updateAlbum(Long albumId, AlbumUpdateRequestDto albumUpdateRequestDto) {
		albumValidator.validateAlbumOwner(albumId, loggedInUserService.getCurrentUserId());
		albumValidator.validateAlbumPathCoordinates(albumUpdateRequestDto);
		Album album = albumRepository.findByIdWithLocations(albumId)
			.orElseThrow(() -> new AlbumNotFoundException(albumId));

		album.setDescription(albumUpdateRequestDto.getDescription());

		Map<Integer, String> currentPhotoUrls = album.getPhotoUrls();
		Map<Integer, String> newPhotoUrls = albumUpdateRequestDto.getPhotoUrls();
		newPhotoUrls.forEach(
			(key, value) -> newPhotoUrls.put(key, s3AndCdnUrlConverter.convertS3UrlOrCdnUrlToPath(value)));

		List<String> deletedS3ObjectKeys = getDeletedS3ObjectKeys(currentPhotoUrls, newPhotoUrls);

		if (!deletedS3ObjectKeys.isEmpty()) {
			s3ImageDeleter.deleteImages(deletedS3ObjectKeys);
		}
		album.setPhotoUrls(newPhotoUrls);

		albumRepository.deleteLocationsByAlbumId(albumId);
		List<LocationUpdateRequestDto> newLocationDtos = albumUpdateRequestDto.getLocations();
		List<Location> newLocations = locationMapper.toEntityListFromUpdateDto(newLocationDtos);
		album.setLocations(newLocations);
		album.bindLocations();

		album.setPath(albumUpdateRequestDto.getPath());

		Album updatedAlbum = albumRepository.save(album);
		return albumMapper.toAlbumReadResponseDto(updatedAlbum);
	}

	private List<String> getDeletedS3ObjectKeys(
		Map<Integer, String> currentPhotoUrls, Map<Integer, String> newPhotoUrls) {
		List<String> deletedS3ObjectKeys = new ArrayList<>();
		for (String currentUrl : currentPhotoUrls.values()) {
			if (!newPhotoUrls.containsValue(currentUrl)) {
				deletedS3ObjectKeys.add(currentUrl);
			}
		}
		return deletedS3ObjectKeys;
	}

	@Transactional
	public void deleteAlbum(Long albumId) {
		albumValidator.validateAlbumOwner(albumId, loggedInUserService.getCurrentUserId());
		albumRepository.findByIdWithLocations(albumId)
			.orElseThrow(() -> new AlbumNotFoundException(albumId));

		albumRepository.deleteLocationsByAlbumId(albumId);
		albumRepository.deleteByAlbumId(albumId);
		postRepository.deletePostByDeletedAlbumId(albumId);
	}

}
