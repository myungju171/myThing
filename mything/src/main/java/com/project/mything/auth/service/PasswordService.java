package com.project.mything.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final PasswordEncoder passwordEncoder;
    private final Random random;

    public Boolean isNotEqualPassword(String rawPassword, String encodedPassword) {
      return !passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public String getRandomCode() {
        StringBuilder randomBuilder = new StringBuilder();
        for (int i = 0; i < 4; i++) randomBuilder.append(random.nextInt(10));
        return randomBuilder.toString();
    }
}
