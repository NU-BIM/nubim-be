package com.soyeon.nubim.security.refreshtoken;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soyeon.nubim.security.jwt.JwtTokenResponseDto;
import com.soyeon.nubim.security.jwt.dto.TokenRenewalRequestDto;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/refresh-tokens")
@RequiredArgsConstructor
public class RefreshTokenControllerV1 {

	private final RefreshTokenService refreshTokenService;

	@PostMapping("/new-access-token")
	public ResponseEntity<JwtTokenResponseDto> getNewAccessToken(
		@RequestBody TokenRenewalRequestDto tokenRenewalRequestDto) {
		return refreshTokenService.renewAccessToken(tokenRenewalRequestDto.getRefreshToken());
	}
}
