package com.soyeon.nubim.security.jwt;

import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.soyeon.nubim.domain.user.User;
import com.soyeon.nubim.domain.user.UserService;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.InvalidKeyException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProvider {

	public static final String ASIA_SEOUL = "Asia/Seoul";

	@Value("${jwt.secret}")
	private String jwtSecret;

	@Value("${jwt.expiration}")
	private int jwtExpirationMs;

	@Value("${jwt.refresh-expiration}")
	private int refreshTokenExpirationMs;

	private SecretKey key;

	private final UserService userService;

	@PostConstruct
	public void init() {
		this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
	}

	public String generateAccessToken(String email, String role) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

		String accessToken = Jwts.builder()
			.setSubject(email)
			.claim("role", role)
			.setIssuedAt(now)
			.setExpiration(expiryDate)
			.signWith(key)
			.compact();

		log.info("Generated access token: {}", accessToken);
		return accessToken;
	}

	public String generateRefreshToken(String email) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + refreshTokenExpirationMs);

		String refreshToken = Jwts.builder()
			.setSubject(email)
			.setIssuedAt(now)
			.setExpiration(expiryDate)
			.signWith(key)
			.compact();

		log.info("Generated refresh token: {}", refreshToken);
		return refreshToken;
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
			return true;
		} catch (
			SecurityException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
			logTokenValidationError(token, e.getMessage());
			return false;
		} catch (ExpiredJwtException e) {
			String errorMessage = createJwtExpirationErrorMessage(e);
			logTokenValidationError(token, errorMessage);
			return false;
		}
	}

	public String getUserEmailFromToken(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
	}

	// refresh 토큰 검증 및 새로운 jwt 토큰 발급 로직
	public String generateNewAccessToken(String refreshToken) {
		if (validateToken(refreshToken)) {
			String userEmail = getUserEmailFromToken(refreshToken);

			User user = userService.findByEmail(userEmail);
			return generateAccessToken(user.getEmail(), user.getRole().name());
		}
		throw new InvalidKeyException("Invalid refresh token");
	}

	private void logTokenValidationError(String token, String e) {
		log.info("token: {}", token);
		log.info("jwt error: {}", e);
	}

	private String createJwtExpirationErrorMessage(ExpiredJwtException e) {
		ZoneId seoulZone = ZoneId.of(ASIA_SEOUL);
		ZonedDateTime expirationTime = ZonedDateTime.ofInstant(e.getClaims().getExpiration().toInstant(), seoulZone);
		ZonedDateTime currentTime = ZonedDateTime.now(seoulZone);

		long differenceMillis = ChronoUnit.MILLIS.between(expirationTime, currentTime);

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

		return String.format(
			"JWT expired at %s. Current time: %s, a difference of %d milliseconds. Allowed clock skew: 0 milliseconds.",
			formatter.format(expirationTime),
			formatter.format(currentTime),
			differenceMillis
		);
	}

}
