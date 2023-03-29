package com.project.mything.auth.service;

import com.project.mything.auth.dto.AuthDto;
import com.project.mything.auth.mapper.AuthMapper;
import com.project.mything.redis.repository.RedisRepository;
import com.project.mything.exception.BusinessLogicException;
import com.project.mything.exception.ErrorCode;
import com.project.mything.security.jwt.service.JwtTokenProvider;
import com.project.mything.user.entity.Role;
import com.project.mything.user.entity.User;
import com.project.mything.user.entity.UserRole;
import com.project.mything.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthMapper authMapper;
    private final AuthNumSendService authNumSendService;
    private final PasswordService passwordService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisRepository redisRepository;
    private final UserService userService;

    public Boolean sendAuthNumber(AuthDto.RequestAuthNumber requestAuthNumber) {
        if (redisRepository.getData(requestAuthNumber.getPhone()).isPresent())
            throw new BusinessLogicException(ErrorCode.MESSAGE_ALREADY_SEND);
        String phone = requestAuthNumber.getPhone();
        duplicatePhone(phone);
        String randomCode = passwordService.getRandomCode();
        authNumSendService.send(phone, randomCode);
        redisRepository.saveData(phone, randomCode, 1000 * 60 * 3L);
        return Boolean.TRUE;
    }

    private void duplicatePhone(String phone) {
        if (userService.findByPhone(phone))
            throw new BusinessLogicException(ErrorCode.PHONE_ALREADY_EXIST);
    }

    public AuthDto.ResponseLogin join(AuthDto.RequestJoin requestJoin) {
        verifiedRandomCode(requestJoin.getPhone(), requestJoin.getAuthNumber());
        User user = authMapper.toUser(requestJoin);
        user.encodePassword(passwordService);
        addUserRole(user);
        User dbUser = userService.saveUser(user);
        return authMapper.toResponseToken(dbUser, jwtTokenProvider.createToken(dbUser));
    }

    private void addUserRole(User user) {
        Role role = Role.builder().build();
        UserRole userRole = UserRole.builder().role(role).user(user).build();
        role.getUserRoles().add(userRole);
        user.getUserRoles().add(userRole);
    }

    private void verifiedRandomCode(String phone, String authNumber) {
        String randomCode = redisRepository.getData(phone)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.NO_MATCH_PHONE_NUMBER));
        if (!randomCode.equals(authNumber))
            throw new BusinessLogicException(ErrorCode.NO_MATCH_AUTH_NUMBER);
    }

    public AuthDto.ResponseLogin login(AuthDto.RequestLogin requestLogin) {
        User dbUser = userService.findUserByEmail(requestLogin.getEmail());
        passwordService.validatePassword(requestLogin.getPassword(), dbUser.getPassword());
        return authMapper.toResponseToken(dbUser, jwtTokenProvider.createToken(dbUser));
    }

    public void duplicateEmail(String email) {
        if (userService.duplicateEmail(email))
            throw new BusinessLogicException(ErrorCode.EMAIL_ALREADY_EXIST);
    }

    public void findPassword(AuthDto.RequestFindPassword requestFindPassword) {
        verifiedRandomCode(requestFindPassword.getPhone(), requestFindPassword.getAuthNumber());
        User dbUser = userService.findUserByPhone(requestFindPassword.getPhone());
        dbUser.changePassword(requestFindPassword.getNewPassword());
        dbUser.encodePassword(passwordService);
    }
}
