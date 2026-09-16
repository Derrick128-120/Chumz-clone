package com.chumzclone.service;

import com.chumzclone.entity.User;
import com.chumzclone.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(String fullName, String email, String phoneNumber, String rawPassword) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("An account with this email already exists");
        }
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException("An account with this phone number already exists");
        }
        User user = User.builder()
                .fullName(fullName)
                .email(email)
                .phoneNumber(phoneNumber)
                .password(passwordEncoder.encode(rawPassword))
                .build();
        return userRepository.save(user);
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
