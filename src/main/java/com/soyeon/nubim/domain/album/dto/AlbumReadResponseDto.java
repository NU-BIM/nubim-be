package com.soyeon.nubim.domain.album.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AlbumReadResponseDto {
	private Long albumId;
	private Long userId;
	private String description;
	private Map<Integer, String> photoUrls;
	private List<LocationReadResponseDto> locations;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
