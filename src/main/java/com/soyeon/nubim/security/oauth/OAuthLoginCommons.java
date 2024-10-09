package com.soyeon.nubim.security.oauth;

import org.springframework.stereotype.Component;

import com.soyeon.nubim.common.enums.Provider;
import com.soyeon.nubim.domain.user.User;
import com.soyeon.nubim.domain.user.exception.EmailAlreadyExistsException;
import com.soyeon.nubim.security.jwt.JwtTokenProvider;
import com.soyeon.nubim.security.jwt.dto.JwtTokenResponseDto;
import com.soyeon.nubim.security.oauth.exception.InvalidTokenException;
import com.soyeon.nubim.security.refreshtoken.RefreshTokenService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuthLoginCommons {

	private final JwtTokenProvider jwtTokenProvider;
	private final RefreshTokenService refreshTokenService;

	protected String parseBearerToken(String token) {
		if (!token.startsWith("Bearer ")) {
			throw new InvalidTokenException("Token must start with 'Bearer '");
		}
		return token.substring(7);
	}

	protected JwtTokenResponseDto generateTokenResponse(User user) {
		String userId = user.getUserId().toString();
		String userEmail = user.getEmail();
		String userRole = user.getRole().name();

		String accessToken = jwtTokenProvider.generateAccessToken(userId, userEmail, userRole);
		String refreshToken = jwtTokenProvider.generateRefreshToken(userId, userEmail, userRole);
		refreshTokenService.upsertRefreshTokenEntity(refreshToken, userEmail);

		return new JwtTokenResponseDto(accessToken, refreshToken);
	}

	protected void validateUserProvider(User user, Provider provider) {
		if (!user.getProvider().equals(provider)) {
			throw new EmailAlreadyExistsException(user.getEmail());
		}
	}
}
