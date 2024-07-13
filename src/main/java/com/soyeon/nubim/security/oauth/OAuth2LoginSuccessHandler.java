package com.soyeon.nubim.security.oauth;

import java.io.IOException;
import java.time.Duration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.soyeon.nubim.domain.user.User;
import com.soyeon.nubim.security.jwt.JwtTokenProvider;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

	private final JwtTokenProvider jwtTokenProvider;
	private final CustomOAuth2UserService customOAuth2UserService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException {
		OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
		String email = (String)oAuth2User.getAttributes().get("email");

		User user = customOAuth2UserService.processOAuth2User(email, oAuth2User);

		String accessToken = jwtTokenProvider.generateAccessToken(user);
		String refreshToken = jwtTokenProvider.generateRefreshToken(user.getEmail());

		response.setContentType("application/json;charset=UTF-8");
		response.setHeader("Authorization", "Bearer " + accessToken);

		ResponseCookie responseCookie = ResponseCookie.from("refresh_token", refreshToken)
			.httpOnly(true)
			.secure(true)
			.path("/")
			.maxAge(Duration.ofDays(7))
			.sameSite("Strict")
			.build();
		response.setHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
		response.getWriter().write("{\"accessToken\":\"" + accessToken + "\"}");
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
		Authentication authentication) throws IOException, ServletException {
		AuthenticationSuccessHandler.super.onAuthenticationSuccess(request, response, chain, authentication);
	}
}
