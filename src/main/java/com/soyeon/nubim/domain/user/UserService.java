package com.soyeon.nubim.domain.user;

import java.util.Optional;

import org.springframework.http.ResponseCookie;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.soyeon.nubim.security.refreshtoken.RefreshTokenService;

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
			.orElseThrow(() -> new UserNotFoundException(email));
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

	public void validateUserExists(Long userId) {
		if (!userRepository.existsById(userId)) {
			throw new UserNotFoundException(userId);
		}
	}

	public User findUserByIdOrThrow(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new UserNotFoundException(userId));
	}

	public String getCurrentUserEmail() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	public User getCurrentUser() {
		String currentUserEmail = getCurrentUserEmail();

		return userRepository.findByEmail(currentUserEmail)
			.orElseThrow(() -> new UserNotFoundException(currentUserEmail));
	}

	public Long getCurrentUserId() {
		String currentUserEmail = getCurrentUserEmail();

		return userRepository.findUserIdByEmail(currentUserEmail)
			.orElseThrow(() -> new UserNotFoundException(currentUserEmail));
	}
}
