package com.soyeon.nubim.security.oauth;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import org.springframework.http.ResponseCookie;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.soyeon.nubim.common.enums.Role;
import com.soyeon.nubim.domain.user.User;
import com.soyeon.nubim.domain.user.UserService;
import com.soyeon.nubim.security.jwt.JwtTokenProvider;
import com.soyeon.nubim.security.refreshtoken.RefreshToken;
import com.soyeon.nubim.security.refreshtoken.RefreshTokenService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	private final UserService userService;
	private final JwtTokenProvider jwtTokenProvider;
	private final RefreshTokenService refreshTokenService;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
		OAuth2User oAuth2User = delegate.loadUser(userRequest);

		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		String userNameAttributeName = userRequest.getClientRegistration()
			.getProviderDetails()
			.getUserInfoEndpoint()
			.getUserNameAttributeName();

		OAuthAttributes attributes = OAuthAttributes.of(
			registrationId,
			userNameAttributeName,
			oAuth2User.getAttributes());

		User user = saveOrUpdate(attributes);

		String accessToken = jwtTokenProvider.generateAccessToken(user);
		String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

		saveRefreshToken(user.getEmail(), refreshToken);

		setTokenInResponse(accessToken);
		setRefreshTokenCookie(refreshToken);

		return new DefaultOAuth2User(
			Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
			attributes.getAttributes(),
			attributes.getNameAttributeKey());
	}

	private User saveOrUpdate(OAuthAttributes attributes) {
		User user;
		try {
			user = userService.findByEmail(attributes.getEmail());
			user.update(attributes.getName(), attributes.getEmail(), attributes.getProfileImageUrl());
		} catch (EntityNotFoundException e) {
			user = attributes.toEntity();
		}

		return userService.saveUser(user);
	}

	protected void saveRefreshToken(String email, String refreshToken) {
		RefreshToken refreshTokenEntity;
		try {
			refreshTokenEntity = refreshTokenService.findByEmail(email);
			refreshTokenEntity.updateToken(refreshToken);
			log.info("Exist refresh token found, update: {}", refreshTokenEntity.getToken());
		} catch (EntityNotFoundException e) {
			refreshTokenEntity = new RefreshToken(refreshToken, email, LocalDateTime.now().plusDays(7));
			log.info("No Exist refresh token found, create: {}", refreshTokenEntity.getToken());
		}

		refreshTokenService.saveRefreshToken(refreshTokenEntity);
	}

	private void setTokenInResponse(String accessToken) {
		ServletRequestAttributes attr = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
		if (attr != null) {
			HttpServletResponse response = attr.getResponse();
			if (response != null) {
				response.setHeader("Authorization", "Bearer " + accessToken);
			}
		}
	}

	private void setRefreshTokenCookie(String refreshToken) {
		ServletRequestAttributes attr = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
		if (attr != null) {
			HttpServletResponse response = attr.getResponse();
			if (response != null) {
				ResponseCookie cookie = ResponseCookie.from("refresh_token", refreshToken)
					.httpOnly(true)
					.secure(true)
					.path("/")
					.maxAge(Duration.ofDays(7))
					.build();
				response.addHeader("Set-Cookie", cookie.toString());
			}
		}
	}

	public User processOAuth2User(String email, OAuth2User oAuth2User) {
		User user;
		try {
			user = userService.findByEmail(email);
		} catch (EntityNotFoundException e) {
			user = createNewUser(email, oAuth2User);
		}
		return userService.saveUser(user);
	}

	private User createNewUser(String email, OAuth2User oAuth2User) {
		String name = (String)oAuth2User.getAttributes().get("name");
		String picture = (String)oAuth2User.getAttributes().get("picture");

		return User.builder()
			.email(email)
			.username(name)
			.nickname(UUID.randomUUID().toString())
			.profileImageUrl(picture)
			.role(Role.USER)
			.build();
	}

}
