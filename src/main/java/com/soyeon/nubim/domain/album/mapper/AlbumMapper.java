package com.soyeon.nubim.domain.album.mapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
	private static final ObjectMapper objectMapper = new ObjectMapper();

	public Album toEntity(AlbumCreateRequestDto albumCreateRequestDto, User user) {
		List<String> photoUrls = albumCreateRequestDto.getPhotoUrls();
		String photoUrlsToJsonString = convertPhotoUrlListToJsonString(photoUrls);

		List<LocationCreateRequestDto> locationDtos = albumCreateRequestDto.getLocations();
		List<Location> locations = locationMapper.toEntityList(locationDtos);

		Album album = Album.builder()
			.user(user)
			.description(albumCreateRequestDto.getDescription())
			.photoUrls(photoUrlsToJsonString)
			.locations(locations)
			.build();

		album.bindLocations();

		return album;
	}

	public AlbumCreateResponseDto toAlbumCreateResponseDto(Album album) {
		String photoUrls = album.getPhotoUrls();
		List<String> photoUrlList = convertJsonStringToPhotoUrlList(photoUrls);

		List<Location> locations = album.getLocations();
		List<LocationCreateResponseDto> locationCreateResponseDtos =
			locationMapper.toLocationCreateResponseDtoList(locations);

		return AlbumCreateResponseDto.builder()
			.albumId(album.getAlbumId())
			.userId(album.getUser().getUserId())
			.description(album.getDescription())
			.photoUrls(photoUrlList)
			.locations(locationCreateResponseDtos)
			.createdAt(album.getCreatedAt())
			.updatedAt(album.getUpdatedAt())
			.build();
	}

	public AlbumReadResponseDto toAlbumReadResponseDto(Album album) {
		String photoUrls = album.getPhotoUrls();
		List<String> photoUrlList = convertJsonStringToPhotoUrlList(photoUrls);

		List<Location> locations = album.getLocations();
		List<LocationReadResponseDto> locationReadResponseDtos =
			locationMapper.toLocationReadResponseDtoList(locations);

		return AlbumReadResponseDto.builder()
			.albumId(album.getAlbumId())
			.userId(album.getUser().getUserId())
			.description(album.getDescription())
			.photoUrls(photoUrlList)
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

	private String convertPhotoUrlListToJsonString(List<String> photoUrls) {
		try {
			return objectMapper.writeValueAsString(photoUrls);
		} catch (JsonProcessingException e) {
			log.info(e.getMessage());
			return "[]";
		}
	}

	private List<String> convertJsonStringToPhotoUrlList(String photoUrls) {
		try {
			return objectMapper.readValue(photoUrls, new TypeReference<>() {
			});
		} catch (JsonProcessingException e) {
			log.info(e.getMessage());
			return Collections.emptyList();
		}
	}

}
