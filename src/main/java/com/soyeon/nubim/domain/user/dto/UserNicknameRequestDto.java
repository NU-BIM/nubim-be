package com.soyeon.nubim.domain.user.dto;

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
	@Size(min = 4, max = 30)
	@Pattern(
		regexp = "^[a-zA-Z][a-zA-Z0-9_.-]+$", // 첫자는 무조건 알파벳
		message = "닉네임은 알파벳 및 숫자, 언더바(_), 점(.), 하이픈(-)만 포함할 수 있습니다. 첫자는 알파벳이어야 합니다.")
	private String nickname;
}
