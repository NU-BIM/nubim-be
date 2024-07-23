package com.soyeon.nubim.security.oauth;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.soyeon.nubim.common.enums.Role;
import com.soyeon.nubim.domain.user.User;

import lombok.Getter;

@Getter
public class GoogleUserInfo {

	private String sub;
	private String name;
	@JsonProperty("given_name")
	private String givenName;
	@JsonProperty("family_name")
	private String familyName;
	private String picture;
	private String email;
	@JsonProperty("email_verified")
	private Boolean emailVerified;

	public User toUserEntity() {
		return User.builder()
			.username(name)
			.nickname(UUID.randomUUID().toString())
			.email(email)
			.profileImageUrl(picture)
			.role(Role.USER)
			.build();
	}

	@Override
	public String toString() {
		return "GoogleUserInfo{" +
			"sub='" + sub + '\'' +
			", name='" + name + '\'' +
			", givenName='" + givenName + '\'' +
			", familyName='" + familyName + '\'' +
			", picture='" + picture + '\'' +
			", email='" + email + '\'' +
			", emailVerified=" + emailVerified +
			'}';
	}
}
