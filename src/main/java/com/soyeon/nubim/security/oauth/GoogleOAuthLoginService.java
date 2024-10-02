package com.soyeon.nubim.security.oauth;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.soyeon.nubim.common.enums.Provider;
import com.soyeon.nubim.domain.user.User;
import com.soyeon.nubim.domain.user.UserService;
import com.soyeon.nubim.domain.user.exception.UserNotFoundException;
import com.soyeon.nubim.security.jwt.dto.JwtTokenResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoogleOAuthLoginService {

	private static final String USER_INFO_ENDPOINT = "https://www.googleapis.com/oauth2/v3/userinfo";
	private final RestTemplate restTemplate = new RestTemplate();

	private final UserService userService;
	private final OAuthLoginCommons oAuthLoginCommons;

	// TODO : redirect 기능 추가 예정
	public ResponseEntity<JwtTokenResponseDto> authenticateWithGoogleToken(String oauthAccessToken) {
		validateOAuthAccessToken(oauthAccessToken);

		GoogleUserInfo userInfo = fetchGoogleUserInfo(oauthAccessToken);

		User user = upsertUserFromGoogleUserInfo(userInfo);

		JwtTokenResponseDto jwtTokenResponseDto = oAuthLoginCommons.generateTokenResponse(user);

		return ResponseEntity.ok()
			.body(jwtTokenResponseDto);
	}

	private static void validateOAuthAccessToken(String oauthAccessToken) {
		if (oauthAccessToken == null) {
			throw new IllegalArgumentException("OAuth Access Token cannot be null");
		}
		if (!oauthAccessToken.startsWith("Bearer ")) {
			throw new IllegalArgumentException("OAuth Access Token must start with 'Bearer '");
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
			oAuthLoginCommons.validateUserProvider(user, Provider.GOOGLE);
			user = user.updateNameFromOAuthProfile(userInfo.getName());
		} catch (UserNotFoundException e) {
			user = userInfo.toUserEntity();
		}
		return userService.saveUser(user);
	}

}
