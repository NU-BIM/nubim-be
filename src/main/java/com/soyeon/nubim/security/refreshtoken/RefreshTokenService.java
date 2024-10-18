package com.soyeon.nubim.security.refreshtoken;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.soyeon.nubim.security.jwt.JwtTokenProvider;
import com.soyeon.nubim.security.jwt.dto.JwtTokenResponseDto;
import com.soyeon.nubim.security.refreshtoken.exception.MultipleRefreshTokensDeletedException;
import com.soyeon.nubim.security.refreshtoken.exception.RefreshTokenNotFoundException;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

	private static final int REFRESH_TOKEN_MAX_AGE_SECONDS = 604800; //60 * 60 * 24 * 7
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

	public void deleteRefreshToken(String email) {
		int deletedTokenCount = refreshTokenRepository.deleteByEmail(email);
		if ( deletedTokenCount == 0 ){
			throw new RefreshTokenNotFoundException(email);
		} else if ( deletedTokenCount >= 2 ){
			throw new MultipleRefreshTokensDeletedException(deletedTokenCount, email);
		}
	}

	public ResponseEntity<JwtTokenResponseDto> renewAccessToken(String refreshToken) {
		validateRefreshToken(refreshToken);

		String newAccessToken = jwtTokenProvider.generateAccessTokenFromRefreshToken(refreshToken);
		String newRefreshToken = renewRefreshToken(refreshToken);

		return ResponseEntity.ok()
			.body(new JwtTokenResponseDto(newAccessToken, newRefreshToken));
	}

	private void validateRefreshToken(String refreshToken) {
		if (!jwtTokenProvider.validateToken(refreshToken)) {
			throw new IllegalArgumentException("Refresh Token is invalid, refreshToken: " + refreshToken);
		}
		if (!isRefreshTokenExist(refreshToken)) {
			throw new EntityNotFoundException("Refresh Token not found in database, refreshToken: " + refreshToken);
		}
	}

	private String renewRefreshToken(String refreshToken) {
		if(jwtTokenProvider.checkRefreshTokenExpiration(refreshToken)){
			String newRefreshToken = jwtTokenProvider.generateRefreshTokenFromRefreshToken(refreshToken);
			upsertRefreshTokenEntity(newRefreshToken, jwtTokenProvider.getUserEmailFromToken(newRefreshToken));
			return newRefreshToken;
		}
		return refreshToken;
	}

	public void upsertRefreshTokenEntity(String refreshToken, String userEmail) {
		RefreshToken refreshTokenEntity;
		LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(REFRESH_TOKEN_MAX_AGE_SECONDS);
		try {
			refreshTokenEntity = findByEmail(userEmail);
			refreshTokenEntity.updateToken(refreshToken, expiresAt);
		} catch (EntityNotFoundException e) {

			refreshTokenEntity = new RefreshToken(refreshToken, userEmail, expiresAt);
		}
		saveRefreshToken(refreshTokenEntity);
	}
}
