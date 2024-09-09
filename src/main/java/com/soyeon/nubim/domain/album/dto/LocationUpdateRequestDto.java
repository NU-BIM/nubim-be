package com.soyeon.nubim.domain.album.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LocationUpdateRequestDto {
	private Double latitude;
	private Double longitude;
	private LocalDateTime visitedAt;
	private String placeName;
	private String placeId;
	private List<Integer> photoUrlKeys;
}