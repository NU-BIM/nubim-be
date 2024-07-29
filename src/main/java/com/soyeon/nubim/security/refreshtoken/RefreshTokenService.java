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

	public boolean isRefreshTokenExist(String token) {
		return refreshTokenRepository.existsByToken(token);
	}

	public RefreshToken saveRefreshToken(RefreshToken refreshToken) {
		return refreshTokenRepository.save(refreshToken);
	}

	public void deleteRefreshToken(String token) {
		refreshTokenRepository.deleteByToken(token)
			.orElseThrow(() -> new EntityNotFoundException("Refresh Token not found, token: " + token));
	}

	public ResponseEntity<String> renewAccessToken(String refreshToken) {
		validateRefreshToken(refreshToken);

		String newAccessToken = jwtTokenProvider.generateAccessTokenFromRefreshToken(refreshToken);
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + newAccessToken);

		return ResponseEntity.ok()
			.headers(headers)
			.body("created new access token");
	}

	private void validateRefreshToken(String refreshToken) {
		if (!jwtTokenProvider.validateToken(refreshToken)) {
			throw new IllegalArgumentException("Refresh Token is invalid, refreshToken: " + refreshToken);
		}
		if (!isRefreshTokenExist(refreshToken)) {
			throw new EntityNotFoundException("Refresh Token not found in database, refreshToken: " + refreshToken);
		}
	}
}
