package com.soyeon.nubim.domain.user;

import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.soyeon.nubim.domain.user.dto.UserProfileResponseDto;
import com.soyeon.nubim.domain.user.dto.UserSimpleResponseDto;
import com.soyeon.nubim.domain.user.exception.NicknameAlreadyExistsException;
import com.soyeon.nubim.domain.user.exception.UserNotFoundException;
import com.soyeon.nubim.security.refreshtoken.RefreshTokenService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final RefreshTokenService refreshTokenService;
	private final UserMapper userMapper;

	public UserProfileResponseDto getCurrentUserProfile() {
		User currentUser = getCurrentUser();

		return userMapper.toUserProfileResponseDto(currentUser);
	}

	public Optional<User> findById(Long userId) {
		return userRepository.findById(userId);
	}

	public User findByEmail(String email) {
		return userRepository.findByEmail(email)
			.orElseThrow(() -> UserNotFoundException.forEmail(email));
	}

	public User saveUser(User user) {
		return userRepository.save(user);
	}

	@Transactional
	public Map<String, String> logout(String token) {
		refreshTokenService.deleteRefreshToken(token);

		return Map.of("status", "success",
			"message", "your refresh token deleted");
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
			.orElseThrow(() -> UserNotFoundException.forEmail(currentUserEmail));
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

	@Transactional
	public UserSimpleResponseDto modifyNickname(String newNickname) {
		validateDuplicatedNickname(newNickname);
		User user = getCurrentUser();
		user.setNickname(newNickname);
		userRepository.save(user);
		return userMapper.toUserSimpleResponseDto(user);
	}

	public void validateDuplicatedNickname(String nickname) {
		if (userRepository.existsByNickname(nickname)) {
			throw new NicknameAlreadyExistsException(nickname);
		}
	}

	public Page<UserSimpleResponseDto> searchUserByNickname(Pageable pageable, String query) {
		return userRepository.findByNicknameStartsWith(pageable, query)
			.map(userMapper::toUserSimpleResponseDto);
	}

	public User getUserByNickname(String nickname) {
		return userRepository.findByNickname(nickname)
			.orElseThrow(() -> UserNotFoundException.forNickname(nickname));
	}
}
