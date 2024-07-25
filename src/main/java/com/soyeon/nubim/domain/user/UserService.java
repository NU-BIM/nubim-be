package com.soyeon.nubim.domain.user;

import java.util.Optional;

import org.springframework.http.ResponseCookie;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.soyeon.nubim.domain.userfollow.UserFollow;
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
		return Long.parseLong(parseAuthority("ID_"));
	}

	public String getCurrentUserRole() {
		return parseAuthority("ROLE_");
	}

	private String parseAuthority(String prefix) {
		return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
			.map(GrantedAuthority::getAuthority)
			.filter(authority -> authority.startsWith(prefix))
			.findFirst()
			.map(authority -> authority.substring(prefix.length()))
			.orElse(null);
	}

	public void addFollowerAndFolloweeByUserFollow(UserFollow userFollow) {
		User follower = userFollow.getFollower();
		User followee = userFollow.getFollowee();

		follower.addFollowee(userFollow);
		followee.addFollower(userFollow);
	}

	public void deleteFollowerAndFolloweeByUserFollow(UserFollow userFollow) {
		User follower = userFollow.getFollower();
		User followee = userFollow.getFollowee();

		follower.deleteFollowee(userFollow);
		followee.deleteFollower(userFollow);
	}
}
