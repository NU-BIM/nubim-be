package com.soyeon.nubim.security.jwt;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtTokenProvider jwtTokenProvider;

	private static final List<String> EXCLUDED = List.of(
		"/css", "/js", "/images", "/favicon.ico", "/error",
		"/login", "/logout", "/oauth2", "/swagger-ui", "/v3/api-docs",
		"/v1/users/login", "/v1/users/logout", "/v1/refresh-tokens/new-access-token");

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getRequestURI();
		return EXCLUDED.stream().anyMatch(path::startsWith);
	}

	@Override
	protected void doFilterInternal(
		HttpServletRequest request,
		HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		log.debug("JWT filter started");
		try {
			processJwtAuthentication(request, response);
		} catch (Exception e) {
			log.error("Failed to set user authentication in security context", e);
		}
		filterChain.doFilter(request, response);
		log.debug("JWT filter completed");
	}

	private void processJwtAuthentication(HttpServletRequest request, HttpServletResponse response) {
		String jwt = getJwtFromRequest(request);
		log.debug("-- Extracted JWT token: {}", jwt);

		if (!isValidJwtToken(jwt)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}

		String userEmail = jwtTokenProvider.getUserEmailFromToken(jwt);
		log.debug("---- JWT token is valid, user email: {}", userEmail);
		setAuthentication(request, jwt);
	}

	private boolean isValidJwtToken(String jwt) {
		if (!StringUtils.hasText(jwt) || jwt.equals("null")) {
			log.debug("---- JWT token is empty");
			return false;
		}
		if (!jwtTokenProvider.validateToken(jwt)) {
			log.debug("---- JWT token is invalid");
			return false;
		}
		return true;
	}

	private void setAuthentication(HttpServletRequest request, String jwt) {
		String id = jwtTokenProvider.getUserIdFromToken(jwt);
		String email = jwtTokenProvider.getUserEmailFromToken(jwt);
		String role = jwtTokenProvider.getUserRoleNameFromToken(jwt);

		UserDetails userDetails = generateUserDetails(id, email, role);

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

	private UserDetails generateUserDetails(String id, String email, String role) {
		return org.springframework.security.core.userdetails.User.builder()
			.username(email)
			.password("")
			.authorities(Arrays.asList(
				new SimpleGrantedAuthority("ROLE_" + role),
				new SimpleGrantedAuthority("ID_" + id)))
			.build();
	}

}
