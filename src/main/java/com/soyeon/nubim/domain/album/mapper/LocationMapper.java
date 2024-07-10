package com.soyeon.nubim.domain.album.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.soyeon.nubim.domain.album.Location;
import com.soyeon.nubim.domain.album.dto.LocationCreateRequestDto;
import com.soyeon.nubim.domain.album.dto.LocationCreateResponseDto;
import com.soyeon.nubim.domain.album.dto.LocationReadResponseDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LocationMapper {

	public Location toEntity(LocationCreateRequestDto locationCreateRequestDto) {
		return Location.builder()
			.latitude(locationCreateRequestDto.getLatitude())
			.longitude(locationCreateRequestDto.getLongitude())
			.visitedAt(locationCreateRequestDto.getVisitedAt())
			.placeName(locationCreateRequestDto.getPlaceName())
			.build();
	}

	public LocationCreateResponseDto toLocationCreateResponseDto(Location location) {
		return LocationCreateResponseDto.builder()
			.locationId(location.getLocationId())
			.albumId(location.getAlbum().getAlbumId())
			.latitude(location.getLatitude())
			.longitude(location.getLongitude())
			.visitedAt(location.getVisitedAt())
			.placeName(location.getPlaceName())
			.build();
	}

	public LocationReadResponseDto toLocationReadResponseDto(Location location) {
		return LocationReadResponseDto.builder()
			.locationId(location.getLocationId())
			.albumId(location.getAlbum().getAlbumId())
			.latitude(location.getLatitude())
			.longitude(location.getLongitude())
			.visitedAt(location.getVisitedAt())
			.placeName(location.getPlaceName())
			.build();
	}

	public List<Location> toEntityList(List<LocationCreateRequestDto> locationDtos) {
		List<Location> locations = new ArrayList<>();
		for (LocationCreateRequestDto locationDto : locationDtos) {
			locations.add(toEntity(locationDto));
		}
		return locations;
	}

	public List<LocationCreateResponseDto> toLocationCreateResponseDtoList(List<Location> locations) {
		List<LocationCreateResponseDto> locationCreateResponseDtos = new ArrayList<>(locations.size());
		for (Location location : locations) {
			locationCreateResponseDtos.add(toLocationCreateResponseDto(location));
		}
		return locationCreateResponseDtos;
	}

	public List<LocationReadResponseDto> toLocationReadResponseDtoList(List<Location> locations) {
		List<LocationReadResponseDto> locationReadResponseDtos = new ArrayList<>(locations.size());
		for (Location location : locations) {
			locationReadResponseDtos.add(toLocationReadResponseDto(location));
		}
		return locationReadResponseDtos;
	}

}
