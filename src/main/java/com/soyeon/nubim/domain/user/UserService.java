package com.soyeon.nubim.domain.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }

    public User findUserByIdOrThrow(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
