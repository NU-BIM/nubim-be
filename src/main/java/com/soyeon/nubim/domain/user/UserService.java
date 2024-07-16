package com.soyeon.nubim.domain.user;

import java.util.Optional;

import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import com.soyeon.nubim.security.refreshtoken.RefreshTokenService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final RefreshTokenService refreshTokenService;

	public Optional<User> findById(Long userId) {
		return userRepository.findById(userId);
	}

	public User findByEmail(String email) {
		return userRepository.findByEmail(email)
			.orElseThrow(() -> new EntityNotFoundException(String.format("User with email " + email + " not found")));
	}

	public User saveUser(User user) {
		return userRepository.save(user);
	}

	@Transactional
	public ResponseCookie logout(String token) {
		refreshTokenService.deleteRefreshToken(token);

		return ResponseCookie.from("refresh_token", "")
			.httpOnly(true)
			.secure(true)
			.path("/")
			.maxAge(0)
			.sameSite("Strict")
			.build();
	}

}
