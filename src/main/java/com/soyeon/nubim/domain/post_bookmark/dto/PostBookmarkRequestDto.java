package com.soyeon.nubim.domain.post_bookmark.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Setter
@Builder
@Jacksonized
public class PostBookmarkRequestDto {
	@NotNull
	private Long postId;
}
