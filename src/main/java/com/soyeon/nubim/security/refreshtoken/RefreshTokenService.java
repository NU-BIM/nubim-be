package com.soyeon.nubim.security.refreshtoken;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.soyeon.nubim.security.jwt.JwtTokenProvider;
import com.soyeon.nubim.security.jwt.JwtTokenResponseDto;

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

	public ResponseEntity<JwtTokenResponseDto> renewAccessToken(String refreshToken) {
		validateRefreshToken(refreshToken);

		String newAccessToken = jwtTokenProvider.generateAccessTokenFromRefreshToken(refreshToken);
		JwtTokenResponseDto jwtTokenResponseDto = new JwtTokenResponseDto(newAccessToken, refreshToken);

		return ResponseEntity.ok()
			.body(jwtTokenResponseDto);
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
