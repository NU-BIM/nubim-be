package com.soyeon.nubim.domain.user;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.soyeon.nubim.domain.user.dto.UserNicknameRequestDto;
import com.soyeon.nubim.domain.user.dto.UserProfileResponseDto;
import com.soyeon.nubim.domain.user.dto.UserSimpleResponseDto;
import com.soyeon.nubim.security.jwt.dto.JwtTokenResponseDto;
import com.soyeon.nubim.security.jwt.dto.TokenDeleteRequestDto;
import com.soyeon.nubim.security.oauth.GoogleOAuthLoginService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserControllerV1 {

	private final UserService userService;
	private final GoogleOAuthLoginService googleOAuthLoginService;

	@GetMapping("/me")
	public ResponseEntity<UserProfileResponseDto> getCurrentUserProfile() {
		UserProfileResponseDto currentUserProfile = userService.getCurrentUserProfile();

		return ResponseEntity.ok()
			.body(currentUserProfile);
	}

	@GetMapping("/login")
	public ResponseEntity<JwtTokenResponseDto> login(@RequestHeader("Authorization") String oauthAccessToken) {
		return googleOAuthLoginService.authenticateWithGoogleToken(oauthAccessToken);
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(@RequestBody TokenDeleteRequestDto tokenDeleteRequestDto) {
		Map<String, String> logoutResult = userService.logout(tokenDeleteRequestDto.getRefreshToken());

		return ResponseEntity.ok().body(logoutResult);
	}

	@Operation(description = "닉네임 변경 api, 문제 시 중복 체크 api 와 동일한 에러 반환")
	@PostMapping("/nickname")
	public ResponseEntity<UserSimpleResponseDto> changeNickname(
		@RequestBody @Valid UserNicknameRequestDto userNicknameRequestDto) {
		UserSimpleResponseDto successResponse = userService.modifyNickname(userNicknameRequestDto.getNickname());
		return ResponseEntity.ok().body(successResponse);
	}

	@Operation(description = "닉네임 중복 및 형식 체크, 중복 시 409, 형식 에러 시 400 반환, 문제 없을 시 200")
	@GetMapping("/nickname/duplication")
	public ResponseEntity<Void> checkNicknameDuplication(
		@RequestParam
		@Size(min = User.NicknamePolicy.MIN_LENGTH, max = User.NicknamePolicy.MAX_LENGTH)
		@Pattern(regexp = User.NicknamePolicy.REGEXP, message = User.NicknamePolicy.ERROR_MESSAGE)
		String nickname) {
		userService.validateDuplicatedNickname(nickname);
		return ResponseEntity.ok().build();
	}
}
