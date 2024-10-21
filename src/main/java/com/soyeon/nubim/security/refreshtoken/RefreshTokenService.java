package com.soyeon.nubim.security.refreshtoken;

import java.time.LocalDateTime;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import com.soyeon.nubim.common.enums.TokenValidationResult;
import com.soyeon.nubim.security.jwt.JwtTokenProvider;
import com.soyeon.nubim.security.jwt.dto.JwtTokenResponseDto;
import com.soyeon.nubim.security.refreshtoken.exception.ExpiredRefreshTokenException;
import com.soyeon.nubim.security.refreshtoken.exception.InvalidRefreshTokenException;
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
	private final TransactionTemplate transactionTemplate;

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

	@Transactional
	public ResponseEntity<JwtTokenResponseDto> renewAccessToken(String refreshToken) {
		validateRefreshToken(refreshToken);

		String newAccessToken = jwtTokenProvider.generateAccessTokenFromRefreshToken(refreshToken);
		String newRefreshToken = renewRefreshToken(refreshToken);

		return ResponseEntity.ok()
			.body(new JwtTokenResponseDto(newAccessToken, newRefreshToken));
	}

	private void validateRefreshToken(String refreshToken) {
		TokenValidationResult validationResult = jwtTokenProvider.validateToken(refreshToken);
		if (validationResult == TokenValidationResult.INVALID) {
			throw new InvalidRefreshTokenException(refreshToken);
		}
		if (validationResult == TokenValidationResult.EXPIRED) {
			deleteExpiredRefreshToken(refreshToken);
			throw new ExpiredRefreshTokenException(refreshToken);
		}
		if (!isRefreshTokenExist(refreshToken)) {
			throw new RefreshTokenNotFoundException(refreshToken);
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

	private void deleteExpiredRefreshToken(String refreshToken) {
		transactionTemplate.executeWithoutResult(status -> refreshTokenRepository.deleteByRefreshToken(refreshToken));
	}
}
