package com.soyeon.nubim.domain.album.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhotoInitialUploadRequestDto {
	private List<String> contentTypes;
}
