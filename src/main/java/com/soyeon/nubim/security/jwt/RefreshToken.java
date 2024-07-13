package com.soyeon.nubim.security.jwt;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class RefreshToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String token;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	private LocalDateTime expiresAt;

	public RefreshToken(String token, String email, LocalDateTime expiresAt) {
		this.token = token;
		this.email = email;
		this.expiresAt = expiresAt;
	}

	public void updateToken(String token) {
		this.token = token;
	}
}
