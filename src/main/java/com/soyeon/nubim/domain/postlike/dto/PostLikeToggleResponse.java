package com.soyeon.nubim.domain.postlike.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostLikeToggleResponse implements PostLikeResponse {
	private String message;
	private Boolean likeResult;
}
