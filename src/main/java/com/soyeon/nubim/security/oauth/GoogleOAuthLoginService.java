package com.soyeon.nubim.security.oauth;

import java.time.LocalDateTime;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.soyeon.nubim.domain.user.User;
import com.soyeon.nubim.domain.user.UserNotFoundException;
import com.soyeon.nubim.domain.user.UserService;
import com.soyeon.nubim.security.jwt.JwtTokenProvider;
import com.soyeon.nubim.security.refreshtoken.RefreshToken;
import com.soyeon.nubim.security.refreshtoken.RefreshTokenService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoogleOAuthLoginService {

	private static final String USER_INFO_ENDPOINT = "https://www.googleapis.com/oauth2/v3/userinfo";
	public static final int REFRESH_TOKEN_MAX_AGE_SECONDS = 604800; //60 * 60 * 24 * 7
	private final RestTemplate restTemplate = new RestTemplate();

	private final UserService userService;
	private final JwtTokenProvider jwtTokenProvider;
	private final RefreshTokenService refreshTokenService;

	// TODO : redirect 기능 추가 예정
	public ResponseEntity<String> authenticateWithGoogleToken(String oauthAccessToken) {
		validateOAuthAccessToken(oauthAccessToken);

		GoogleUserInfo userInfo = fetchGoogleUserInfo(oauthAccessToken);

		User user = upsertUserFromGoogleUserInfo(userInfo);

		HttpHeaders headers = createAccessTokenHeader(user);
		ResponseCookie refreshTokenCookie = createRefreshTokenCookie(user);

		return ResponseEntity.ok()
			.headers(headers)
			.header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
			.body("success");
	}

	private static void validateOAuthAccessToken(String oauthAccessToken) {
		if (oauthAccessToken == null) {
			throw new IllegalArgumentException("oauthAccessToken cannot be null");
		}
		if (!oauthAccessToken.startsWith("Bearer ")) {
			throw new IllegalArgumentException("oauthAccessToken must start with 'Bearer '");
		}
	}

	private GoogleUserInfo fetchGoogleUserInfo(String oauthAccessToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(parseBearerToken(oauthAccessToken));
		HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

		return restTemplate.exchange(
			USER_INFO_ENDPOINT,
			HttpMethod.GET,
			entity,
			GoogleUserInfo.class
		).getBody();
	}

	private static String parseBearerToken(String accessToken) {
		return accessToken.substring(7);
	}

	private User upsertUserFromGoogleUserInfo(GoogleUserInfo userInfo) {
		User user;
		try {
			user = userService.findByEmail(userInfo.getEmail());
			user = user.updateNameFromOAuthProfile(userInfo.getName());
		} catch (UserNotFoundException e) {
			user = userInfo.toUserEntity();
		}
		return userService.saveUser(user);
	}

	private HttpHeaders createAccessTokenHeader(User user) {
		String accessToken = jwtTokenProvider.generateAccessToken(user.getEmail(), user.getRole().name());
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Bearer " + accessToken);
		return headers;
	}

	private ResponseCookie createRefreshTokenCookie(User user) {
		String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

		upsertRefreshTokenEntity(refreshToken, user.getEmail());

		return ResponseCookie.from("refresh_token", refreshToken)
			.httpOnly(true)
			.secure(true)
			.path("/")
			.maxAge(REFRESH_TOKEN_MAX_AGE_SECONDS)
			.sameSite("Strict")
			.build();
	}

	private void upsertRefreshTokenEntity(String refreshToken, String userEmail) {
		RefreshToken refreshTokenEntity;
		LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(REFRESH_TOKEN_MAX_AGE_SECONDS);
		try {
			refreshTokenEntity = refreshTokenService.findByEmail(userEmail);
			refreshTokenEntity.updateToken(refreshToken, expiresAt);
		} catch (EntityNotFoundException e) {

			refreshTokenEntity = new RefreshToken(refreshToken, userEmail, expiresAt);
		}
		refreshTokenService.saveRefreshToken(refreshTokenEntity);
	}

}
