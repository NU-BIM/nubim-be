package com.soyeon.nubim.domain.user.dto;

import com.soyeon.nubim.domain.user.User;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserNicknameRequestDto {
	@NotNull
	@Size(min = User.NicknamePolicy.MIN_LENGTH, max = User.NicknamePolicy.MAX_LENGTH)
	@Pattern(
		regexp = User.NicknamePolicy.REGEXP,
		message = User.NicknamePolicy.ERROR_MESSAGE)
	private String nickname;
}
