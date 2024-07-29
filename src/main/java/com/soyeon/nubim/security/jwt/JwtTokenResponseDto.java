package com.soyeon.nubim.security.jwt;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class JwtTokenResponseDto {
	private static final String BEARER_TOKEN_PREFIX = "Bearer";
	private static final int ACCESS_TOKEN_EXPIRES_IN_SECONDS = 3600;

	@JsonProperty("access_token")
	private String accessToken;

	@JsonProperty("token_type")
	private String tokenType;

	@JsonProperty("expires_in")
	private long expiresIn;

	@JsonProperty("refresh_token")
	private String refreshToken;

	public JwtTokenResponseDto(String accessToken, String refreshToken) {
		this.accessToken = accessToken;
		this.tokenType = BEARER_TOKEN_PREFIX;
		this.expiresIn = ACCESS_TOKEN_EXPIRES_IN_SECONDS;
		this.refreshToken = refreshToken;
	}
}
