package com.soyeon.nubim.security.refreshtoken;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.soyeon.nubim.security.jwt.JwtTokenProvider;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

	private final RefreshTokenRepository refreshTokenRepository;
	private final JwtTokenProvider jwtTokenProvider;

	public RefreshToken findByEmail(String email) {
		return refreshTokenRepository.findByEmail(email)
			.orElseThrow(() -> new EntityNotFoundException("Refresh Token not found, email: " + email));
	}

	public RefreshToken saveRefreshToken(RefreshToken refreshToken) {
		return refreshTokenRepository.save(refreshToken);
	}

	public void deleteRefreshToken(String token) {
		refreshTokenRepository.deleteByToken(token)
			.orElseThrow(() -> new EntityNotFoundException("Refresh Token not found, token: " + token));
	}

	public ResponseEntity<String> generateNewAccessToken(String refreshToken) {
		String newAccessToken = jwtTokenProvider.generateNewAccessToken(refreshToken);
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + newAccessToken);

		return ResponseEntity.ok()
			.headers(headers)
			.body("created new access token");
	}
}
