package com.soyeon.nubim.domain.user;

import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class UserService {
	private UserRepository userRepository;

	public Optional<User> findById(Long userId) {
		return userRepository.findById(userId);
	}

	public User findByEmail(String email) {
		return userRepository.findByEmail(email)
			.orElseThrow(() -> new EntityNotFoundException(String.format("User with email " + email + " not found")));
	}
}
