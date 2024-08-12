package com.soyeon.nubim.domain.album.dto;

import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AlbumSimpleResponse {
	private Map<Integer, String> photoUrls;
	private List<LocationReadResponseDto> locations;
}
