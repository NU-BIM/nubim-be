package com.soyeon.nubim.security.oauth;

import java.util.Map;
import java.util.UUID;

import com.soyeon.nubim.common.enums.Role;
import com.soyeon.nubim.domain.user.User;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OAuthAttributes {

	private Map<String, Object> attributes;
	private String nameAttributeKey;
	private String name;
	private String email;
	private String nickname;
	private String profileImageUrl;

	@Builder
	public OAuthAttributes(
		Map<String, Object> attributes,
		String nameAttributeKey,
		String name,
		String email,
		String nickname,
		String profileImageUrl) {
		this.attributes = attributes;
		this.nameAttributeKey = nameAttributeKey;
		this.name = name;
		this.email = email;
		this.nickname = nickname;
		this.profileImageUrl = profileImageUrl;
	}

	public static OAuthAttributes of(
		String registrationId,
		String userNameAttributeName,
		Map<String, Object> attributes) {
		return ofGoogle(userNameAttributeName, attributes);
	}

	private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
		return OAuthAttributes.builder()
			.name((String)attributes.get("name"))
			.email((String)attributes.get("email"))
			.profileImageUrl((String)attributes.get("picture"))
			.attributes(attributes)
			.nameAttributeKey(userNameAttributeName)
			.build();
	}

	public User toEntity() {
		return User.builder()
			.username(name)
			.nickname(UUID.randomUUID().toString())
			.email(email)
			.profileImageUrl(profileImageUrl)
			.role(Role.USER)
			.build();
	}
}
