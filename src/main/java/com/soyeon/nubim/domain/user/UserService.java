package com.soyeon.nubim.domain.user;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class UserService {
	private UserRepository userRepository;

	public Optional<User> findById(Long userId) {
		return userRepository.findById(userId);
	}

}
