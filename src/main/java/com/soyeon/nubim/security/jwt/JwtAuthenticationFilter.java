package com.soyeon.nubim.security.jwt;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.soyeon.nubim.security.refreshtoken.RefreshToken;
import com.soyeon.nubim.security.refreshtoken.RefreshTokenRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;
	private final CustomUserDetailsService userDetailsService;
	private final RefreshTokenRepository refreshTokenRepository;

	private static final List<String> EXCLUDED = List.of(
		"/v3/api-docs",
		"/error",
		"/favicon.ico",
		"/login",
		"/logout",
		"/oauth2");

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getRequestURI();
		return (path.startsWith("/swagger-ui/") && !path.startsWith("/swagger-ui/index.html")) ||
			EXCLUDED.stream().anyMatch(path::startsWith);
	}

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		log.info("JWT filter start");
		try {
			processJwtAuthentication(request, response);
		} catch (Exception e) {
			log.error("Could not set user authentication in security context", e);
		}
		filterChain.doFilter(request, response);
		log.info("JWT filter end");
	}

	private void processJwtAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String jwt = getJwtFromRequest(request);
		log.info("-- JWT token: {}", jwt);

		if (!StringUtils.hasText(jwt)) {
			log.info("---- JWT token is empty");
			setResponseUnAuthorized(response, "jwt token not found");
			return;
		}
		if (jwtTokenProvider.validateToken(jwt)) {
			String userEmail = jwtTokenProvider.getUserEmailFromToken(jwt);
			log.info("---- JWT found & Valid, user Email: {}", userEmail);
			setAuthentication(request, userEmail);
			return;
		}

		log.info("---- JWT found, but Invalid");
		processInvalidJwt(request, response);
	}

	private void processInvalidJwt(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String refreshToken = getRefreshTokenFromCookie(request);

		if (!StringUtils.hasText(refreshToken)) {
			log.info("------ refresh token not found");
			setResponseUnAuthorized(response, "refresh token not found");
			return;
		}
		if (!jwtTokenProvider.validateToken(refreshToken)) {
			log.info("------ refresh token Invalid");
			setResponseUnAuthorized(response, "refresh token is invalid");
			return;
		}

		log.info("------ refresh token is valid, refresh token: {}", refreshToken);
		processRefreshTokenAuthentication(request, response, refreshToken);
	}

	public void processRefreshTokenAuthentication(
		HttpServletRequest request,
		HttpServletResponse response,
		String refreshToken) throws IOException {
		String userEmail = jwtTokenProvider.getUserEmailFromToken(refreshToken);
		RefreshToken storedRefreshToken = refreshTokenRepository.findByEmail(userEmail)
			.orElseThrow(() -> new RuntimeException("Stored Refresh token not found"));

		if (!StringUtils.hasText(storedRefreshToken.getToken())) {
			log.info("-------- refresh token is not stored");
			setResponseUnAuthorized(response, "stored token not found");
			return;
		}

		if (!storedRefreshToken.getToken().equals(refreshToken)) {
			log.info("-------- refresh token is not equals storedRefreshToken");
			setResponseUnAuthorized(response, "Refresh token is invalid or expired.");
			return;
		}

		log.info("-------- refresh token is equals storedRefreshToken");
		String newAccessToken = jwtTokenProvider.generateNewAccessToken(refreshToken);
		response.setHeader("Authorization", "Bearer " + newAccessToken);
		// setAuthentication(request, userEmail);
	}

	private void setResponseUnAuthorized(HttpServletResponse response, String s) throws IOException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.getWriter().write(s);
	}

	private void setAuthentication(HttpServletRequest request, String userEmail) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

		UsernamePasswordAuthenticationToken authentication =
			new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
		authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	private String getJwtFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}

	private String getRefreshTokenFromCookie(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if ("refresh_token".equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}
}
