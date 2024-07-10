package com.soyeon.nubim.domain.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class UserService {
    private UserRepository userRepository;

    public Optional<User> findById(Long userId) {
        return userRepository.findById(userId);
    }

    public void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
    }
}
