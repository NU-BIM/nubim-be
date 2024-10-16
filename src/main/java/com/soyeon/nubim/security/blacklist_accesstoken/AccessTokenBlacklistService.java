package com.soyeon.nubim.security.blacklist_accesstoken;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.soyeon.nubim.security.oauth.OAuthLoginCommons;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccessTokenBlacklistService {

	private final OAuthLoginCommons oAuthLoginCommons;
	private final AccessTokenBlacklistRepository accessTokenBlacklistRepository;
	@Value("${jwt.expiration}")
	private int jwtExpirationMs;

	public void addToBlacklist(String accessToken) {
		accessToken = oAuthLoginCommons.parseBearerToken(accessToken);
		AccessTokenBlacklist blacklistedAccessToken = new AccessTokenBlacklist(accessToken, jwtExpirationMs);
		accessTokenBlacklistRepository.save(blacklistedAccessToken);
	}
}