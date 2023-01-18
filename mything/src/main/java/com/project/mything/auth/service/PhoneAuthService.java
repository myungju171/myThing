package com.project.mything.auth.service;

import com.project.mything.auth.dto.PhoneAuthDto;
import com.project.mything.auth.entity.PhoneAuth;
import com.project.mything.auth.mapper.PhoneAuthMapper;
import com.project.mything.auth.repository.PhoneAuthRepository;
import com.project.mything.exception.BusinessLogicException;
import com.project.mything.exception.ErrorCode;
import com.project.mything.user.dto.UserDto;
import com.project.mything.user.entity.User;
import com.project.mything.user.mapper.UserMapper;
import com.project.mything.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.message.model.Message;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@Transactional
@RequiredArgsConstructor
public class PhoneAuthService {

    private final PhoneAuthRepository phoneAuthRepository;
    private final PhoneAuthMapper phoneAuthMapper;
    private final SendService sendService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public String sendAuthNumber(PhoneAuthDto.RequestAuthNumber authNumber) {
        PhoneAuth phoneAuth = phoneAuthMapper.toPhoneAuth(authNumber, getRandomCode());
        Message message = sendService.send(phoneAuth.getPhone(), phoneAuth.getAuthNumber());
        phoneAuthRepository.save(phoneAuth);
        return "true";
    }

    private String getRandomCode() {
        String randomCode = "";
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            randomCode += String.valueOf(random.nextInt(10));
        }
        return randomCode;
    }

    public UserDto.ResponseUserId join(PhoneAuthDto.RequestJoin requestJoin) {
        PhoneAuth dbPhoneAuth = findVerifiedPhoneAuth(requestJoin);
        phoneAuthRepository.delete(dbPhoneAuth);

        if (!dbPhoneAuth.getAuthNumber().equals(requestJoin.getAuthNumber())) {
            throw new BusinessLogicException(ErrorCode.NO_MATCH_AUTH_NUMBER);
        }

        User user = phoneAuthMapper.toUser(requestJoin);

        return userMapper.toResponseUserId(userRepository.save(user));
    }

    private PhoneAuth findVerifiedPhoneAuth(PhoneAuthDto.RequestJoin requestJoin) {
        return phoneAuthRepository
                .findPhoneAuthByPhone(requestJoin.getPhone())
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.NO_MATCH_PHONE_NUMBER));
    }

}
