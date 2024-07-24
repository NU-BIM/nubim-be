package com.soyeon.nubim.security.refreshtoken;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/refresh-tokens")
@RequiredArgsConstructor
public class RefreshTokenControllerV1 {

	private final RefreshTokenService refreshTokenService;

	@PostMapping("/new-access-token")
	public ResponseEntity<String> generateNewAccessToken(@CookieValue("refresh_token") String refreshToken) {
		return refreshTokenService.generateNewAccessToken(refreshToken);
	}
}
