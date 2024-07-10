package com.soyeon.nubim.domain.album.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AlbumCreateRequestDto {
	private Long userId;
	private String description;
	private List<String> photoUrls;
	private List<LocationCreateRequestDto> locations;
}
