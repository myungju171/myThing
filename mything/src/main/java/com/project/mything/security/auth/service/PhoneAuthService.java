package com.project.mything.security.auth.service;

import com.project.mything.security.auth.dto.PhoneAuthDto;
import com.project.mything.security.auth.entity.PhoneAuth;
import com.project.mything.security.auth.mapper.PhoneAuthMapper;
import com.project.mything.security.auth.repository.PhoneAuthRepository;
import com.project.mything.exception.BusinessLogicException;
import com.project.mything.exception.ErrorCode;
import com.project.mything.security.jwt.util.JwtTokenProvider;
import com.project.mything.user.dto.UserDto;
import com.project.mything.user.entity.Role;
import com.project.mything.user.entity.User;
import com.project.mything.user.entity.UserRole;
import com.project.mything.user.mapper.UserMapper;
import com.project.mything.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public String sendAuthNumber(PhoneAuthDto.RequestAuthNumber authNumber) {
        if (authNumber.getPhone().length() != 11) {
            throw new BusinessLogicException(ErrorCode.INVALID_PHONE_NUMBER);
        }
        PhoneAuth phoneAuth = phoneAuthMapper.toPhoneAuth(authNumber, getRandomCode());
        sendService.send(phoneAuth.getPhone(), phoneAuth.getAuthNumber());
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

    public UserDto.ResponseJoinUser join(PhoneAuthDto.RequestJoin requestJoin) {
//        PhoneAuth dbPhoneAuth = findVerifiedPhoneAuth(requestJoin);
//        phoneAuthRepository.delete(dbPhoneAuth);

//        if (!dbPhoneAuth.getAuthNumber().equals(requestJoin.getAuthNumber())) {
//            throw new BusinessLogicException(ErrorCode.NO_MATCH_AUTH_NUMBER);
//        }

//        Optional<User> userByPhone = userRepository.findUserByPhone(requestJoin.getPhone());
//        if (userByPhone.isPresent()) {
//            User dbUser = userByPhone.get();
//            return userMapper.toResponseUserId(dbUser);
//        }

        User user = phoneAuthMapper.toUser(requestJoin);
        String encodedPassword = bCryptPasswordEncoder.encode(requestJoin.getPassword());
        User entity = user.setPassword(encodedPassword);
        BuildUserRole(entity);
        User dbUser = userRepository.save(entity);
        String accessToken = jwtTokenProvider.createToken(dbUser);
        return userMapper.toResponseJoinUser(dbUser,accessToken);
    }

    private void BuildUserRole(User entity) {
        Role build1 = Role.builder().build();
        UserRole build = UserRole.builder()
                .role(build1)
                .user(entity)
                .build();
        build1.getUserRoles().add(build);
        entity.getUserRoles().add(build);

    }

    private PhoneAuth findVerifiedPhoneAuth(PhoneAuthDto.RequestJoin requestJoin) {
        return phoneAuthRepository
                .findPhoneAuthByPhone(requestJoin.getPhone())
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.NO_MATCH_PHONE_NUMBER));
    }


    public UserDto.ResponseJoinUser login(PhoneAuthDto.RequestLogin requestLogin) {
        User dbUser
                = userRepository.findByEmail(requestLogin.getEmail()).orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
        String token = jwtTokenProvider.createToken(dbUser);
        return userMapper.toResponseJoinUser(dbUser, token);
    }
}
