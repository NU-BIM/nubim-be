package com.soyeon.nubim.domain.user;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soyeon.nubim.domain.user.dto.UserProfileResponseDto;
import com.soyeon.nubim.security.jwt.dto.JwtTokenResponseDto;
import com.soyeon.nubim.security.jwt.dto.TokenDeleteRequestDto;
import com.soyeon.nubim.security.oauth.GoogleOAuthLoginService;

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

}
