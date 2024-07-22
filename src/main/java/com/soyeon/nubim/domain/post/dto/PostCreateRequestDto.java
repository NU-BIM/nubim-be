package com.soyeon.nubim.domain.post.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PostCreateRequestDto {
	@NotNull
	private Long albumId;

	@NotNull
	@Size(min = 1, max = 100)
	private String postTitle;

	@NotNull
	@Size(min = 1, max = 2200)
	private String postContent;
}
