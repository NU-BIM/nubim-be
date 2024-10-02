package com.soyeon.nubim.domain.user;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.soyeon.nubim.domain.user.exception.UserNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoggedInUserService {
	private final UserRepository userRepository;

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
}
