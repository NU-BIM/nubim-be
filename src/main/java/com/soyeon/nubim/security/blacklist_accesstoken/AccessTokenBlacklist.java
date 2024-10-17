package com.soyeon.nubim.security.blacklist_accesstoken;

import java.util.concurrent.TimeUnit;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("AccessTokenBlacklist")
public class AccessTokenBlacklist {

	@Id
	private String accessToken;

	@TimeToLive(unit = TimeUnit.MILLISECONDS)
	private int expiration;

}
