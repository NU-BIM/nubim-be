package com.soyeon.nubim.domain.album;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.soyeon.nubim.common.util.aws.S3PresignedUrlGenerator;
import com.soyeon.nubim.domain.album.dto.AlbumCreateRequestDto;
import com.soyeon.nubim.domain.album.dto.AlbumCreateResponseDto;
import com.soyeon.nubim.domain.album.dto.AlbumReadResponseDto;
import com.soyeon.nubim.domain.album.mapper.AlbumMapper;
import com.soyeon.nubim.domain.user.User;
import com.soyeon.nubim.domain.user.UserService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AlbumService {

	private final S3PresignedUrlGenerator s3PresignedUrlGenerator;
	private final AlbumRepository albumRepository;
	private final AlbumMapper albumMapper;
	private final UserService userService;

	public AlbumCreateResponseDto createAlbum(AlbumCreateRequestDto albumCreateRequestDto) {
		User user = userService.findById(albumCreateRequestDto.getUserId())
			.orElseThrow(() -> new EntityNotFoundException("User not found, id: " + albumCreateRequestDto.getUserId()));

		Album album = albumMapper.toEntity(albumCreateRequestDto, user);
		List<Location> locations = album.getLocations();
		for (Location location : locations) {
			location.setAlbum(album);
		}

		Album savedAlbum = albumRepository.save(album);

		return albumMapper.toAlbumCreateResponseDto(savedAlbum);
	}

	public AlbumReadResponseDto findByIdWithLocations(Long albumId) {
		Album album = albumRepository.findByIdWithLocations(albumId)
			.orElseThrow(() -> new EntityNotFoundException("Album not found, albumId: " + albumId));

		return albumMapper.toAlbumReadResponseDto(album);
	}

	public List<AlbumReadResponseDto> findAlbumsByUserId(Long userId) {
		List<Album> albums = albumRepository.findByUserUserId(userId);

		List<AlbumReadResponseDto> albumReadResponseDtos = new ArrayList<>(albums.size());
		for (Album album : albums) {
			albumReadResponseDtos.add(albumMapper.toAlbumReadResponseDto(album));
		}
		return albumReadResponseDtos;
	}

	public void deleteAlbum(Long albumId) {
		albumRepository.deleteById(albumId);
	}

	public List<String> handlePhotoUploadUrlsGeneration(List<String> contentTypes) {
		return s3PresignedUrlGenerator.generatePresignedUrls(contentTypes);
	}

}
