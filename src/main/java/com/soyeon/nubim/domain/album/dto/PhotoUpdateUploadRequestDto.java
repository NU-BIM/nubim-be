package com.soyeon.nubim.domain.album.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhotoUpdateUploadRequestDto {
	private List<String> contentTypes;
	private String uploadPath;
}
