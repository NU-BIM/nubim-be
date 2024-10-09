package com.soyeon.nubim.security.oauth;

import java.util.Base64;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soyeon.nubim.common.enums.Provider;
import com.soyeon.nubim.common.enums.Role;
import com.soyeon.nubim.domain.user.User;
import com.soyeon.nubim.domain.user.UserNicknameGenerator;
import com.soyeon.nubim.domain.user.UserService;
import com.soyeon.nubim.domain.user.exception.UserNotFoundException;
import com.soyeon.nubim.security.jwt.dto.JwtTokenResponseDto;
import com.soyeon.nubim.security.oauth.exception.InvalidAppleIdTokenException;
import com.soyeon.nubim.security.oauth.exception.TokenProcessingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppleOAuthLoginService {

	private final AppleIdTokenValidator appleIdTokenValidator;
	private final UserService userService;
	private final OAuthLoginCommons oAuthLoginCommons;

	public ResponseEntity<JwtTokenResponseDto> authenticateWithAppleToken(String idToken) {
		idToken = oAuthLoginCommons.parseBearerToken(idToken);
		appleIdTokenValidator.validateAppleIdToken(idToken);
		User user = fetchAppleUserInfo(idToken);

		user = upsertUserFromAppleUserInfo(user);
		JwtTokenResponseDto jwtTokenResponseDto = oAuthLoginCommons.generateTokenResponse(user);

		return ResponseEntity.ok()
			.body(jwtTokenResponseDto);
	}

	private User fetchAppleUserInfo(String idToken) {
		String email = extractEmailFromToken(idToken);
		String name = email.split("@")[0];

		return User.builder()
			.username(name)
			.nickname(UserNicknameGenerator.generate())
			.email(email)
			.role(Role.USER)
			.provider(Provider.APPLE)
			.build();
	}

	private String extractEmailFromToken(String idToken) {
		try {
			String[] parts = idToken.split("\\.");
			String payload = new String(Base64.getUrlDecoder().decode(parts[1]));

			ObjectMapper mapper = new ObjectMapper();
			Map<String, String> payloadMap = mapper.readValue(payload, Map.class);

			String email = payloadMap.get("email");
			if (email == null || email.isEmpty()) {
				throw new InvalidAppleIdTokenException("Email claim is empty");
			}
			return email;
		} catch (Exception e) {
			throw new TokenProcessingException("Failed to extract email from token" + e.getMessage());
		}
	}

	private User upsertUserFromAppleUserInfo(User user) {
		try {
			User existingUser = userService.findByEmail(user.getEmail());
			oAuthLoginCommons.validateUserProvider(existingUser, Provider.APPLE);
			existingUser.updateNameFromOAuthProfile(user.getUsername());
			return userService.saveUser(existingUser);
		} catch (UserNotFoundException e) {
			return userService.saveUser(user);
		}
	}

}
