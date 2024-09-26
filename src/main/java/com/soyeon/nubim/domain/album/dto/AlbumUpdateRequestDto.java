package com.soyeon.nubim.domain.album.dto;

import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AlbumUpdateRequestDto {
	private String description;
	private Map<Integer, String> photoUrls;
	private List<LocationUpdateRequestDto> locations;
	private List<List<Double>> path;
}
