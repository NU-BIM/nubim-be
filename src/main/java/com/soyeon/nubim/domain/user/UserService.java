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

    public void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
    }
    public User findUserByIdOrThrow(Long userId) {
        return userRepository
                .findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }
}
