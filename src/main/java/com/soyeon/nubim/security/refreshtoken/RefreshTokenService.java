package com.soyeon.nubim.security.refreshtoken;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

	private final RefreshTokenRepository refreshTokenRepository;

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
}
