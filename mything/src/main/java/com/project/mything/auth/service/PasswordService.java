package com.project.mything.auth.service;

import com.project.mything.exception.BusinessLogicException;
import com.project.mything.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final PasswordEncoder passwordEncoder;
    private final Random random;

    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    public String getRandomCode() {
        StringBuilder randomBuilder = new StringBuilder();
        for (int i = 0; i < 4; i++) randomBuilder.append(random.nextInt(10));
        return randomBuilder.toString();
    }

    public void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword))
            throw new BusinessLogicException(ErrorCode.NO_CORRECT_ACCOUNT);
    }
}
