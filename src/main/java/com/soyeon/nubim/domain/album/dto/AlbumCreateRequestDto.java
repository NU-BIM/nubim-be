package com.soyeon.nubim.domain.album.dto;

import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AlbumCreateRequestDto implements AlbumRequestDto {
	private String description;
	private Map<Integer, String> photoUrls;
	private List<LocationCreateRequestDto> locations;
	private List<List<Double>> path;
}
