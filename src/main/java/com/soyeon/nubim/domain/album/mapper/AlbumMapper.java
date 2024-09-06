package com.soyeon.nubim.domain.album.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.soyeon.nubim.common.util.aws.S3AndCdnUrlConverter;
import com.soyeon.nubim.domain.album.Album;
import com.soyeon.nubim.domain.album.Location;
import com.soyeon.nubim.domain.album.dto.AlbumCreateRequestDto;
import com.soyeon.nubim.domain.album.dto.AlbumCreateResponseDto;
import com.soyeon.nubim.domain.album.dto.AlbumReadResponseDto;
import com.soyeon.nubim.domain.album.dto.LocationCreateRequestDto;
import com.soyeon.nubim.domain.album.dto.LocationCreateResponseDto;
import com.soyeon.nubim.domain.album.dto.LocationReadResponseDto;
import com.soyeon.nubim.domain.user.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlbumMapper {

	private final LocationMapper locationMapper;
	private final S3AndCdnUrlConverter s3AndCdnUrlConverter;

	public Album toEntity(AlbumCreateRequestDto albumCreateRequestDto, User user) {
		Map<Integer, String> photoUrls = albumCreateRequestDto.getPhotoUrls();

		// S3 url 제거 후 경로만 저장
		photoUrls.forEach((key, value) -> photoUrls.put(key, s3AndCdnUrlConverter.convertS3UrlToPath(value)));

		List<LocationCreateRequestDto> locationDtos = albumCreateRequestDto.getLocations();
		List<Location> locations = locationMapper.toEntityListFromCreateDto(locationDtos);

		Album album = Album.builder()
			.user(user)
			.description(albumCreateRequestDto.getDescription())
			.photoUrls(photoUrls)
			.locations(locations)
			.build();

		album.bindLocations();

		return album;
	}

	public AlbumCreateResponseDto toAlbumCreateResponseDto(Album album) {
		Map<Integer, String> photoUrls = album.getPhotoUrls();

		List<Location> locations = album.getLocations();
		List<LocationCreateResponseDto> locationCreateResponseDtos =
			locationMapper.toLocationCreateResponseDtoList(locations);

		return AlbumCreateResponseDto.builder()
			.albumId(album.getAlbumId())
			.userId(album.getUser().getUserId())
			.description(album.getDescription())
			.photoUrls(photoUrls)
			.locations(locationCreateResponseDtos)
			.createdAt(album.getCreatedAt())
			.updatedAt(album.getUpdatedAt())
			.build();
	}

	public AlbumReadResponseDto toAlbumReadResponseDto(Album album) {
		Map<Integer, String> photoUrls = album.getPhotoUrls();

		// cdn 경로 붙여서 반환
		photoUrls.forEach((key, value) -> photoUrls.put(key, s3AndCdnUrlConverter.convertPathToCdnUrl(value)));

		List<Location> locations = album.getLocations();
		List<LocationReadResponseDto> locationReadResponseDtos =
			locationMapper.toLocationReadResponseDtoList(locations);

		return AlbumReadResponseDto.builder()
			.albumId(album.getAlbumId())
			.userId(album.getUser().getUserId())
			.description(album.getDescription())
			.photoUrls(photoUrls)
			.locations(locationReadResponseDtos)
			.createdAt(album.getCreatedAt())
			.updatedAt(album.getUpdatedAt())
			.build();
	}

	public List<AlbumReadResponseDto> toAlbumReadResponseDtoList(List<Album> albums) {
		List<AlbumReadResponseDto> albumReadResponseDtos = new ArrayList<>(albums.size());
		for (Album album : albums) {
			albumReadResponseDtos.add(toAlbumReadResponseDto(album));
		}
		return albumReadResponseDtos;
	}

}
