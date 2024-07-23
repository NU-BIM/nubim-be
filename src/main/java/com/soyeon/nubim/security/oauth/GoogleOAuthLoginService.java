package com.soyeon.nubim.security.oauth;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.soyeon.nubim.domain.user.User;
import com.soyeon.nubim.domain.user.UserNotFoundException;
import com.soyeon.nubim.domain.user.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoogleOAuthLoginService {

	private static final String USER_INFO_ENDPOINT = "https://www.googleapis.com/oauth2/v3/userinfo";
	private final RestTemplate restTemplate = new RestTemplate();

	private final UserService userService;

	public ResponseEntity<String> authenticateWithGoogleToken(String oauthAccessToken) {
		validateOAuthAccessToken(oauthAccessToken);

		GoogleUserInfo userInfo = fetchGoogleUserInfo(oauthAccessToken);

		User user = upsertUserFromGoogleUserInfo(userInfo);

		return ResponseEntity.ok()
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

}
