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
        String randomCode = passwordService.getRandomCode();
        authNumSendService.send(requestAuthNumber.getPhone(), randomCode);
        redisRepository.saveData(requestAuthNumber.getPhone(), randomCode, 1000 * 60 * 3L);
        return Boolean.TRUE;
    }

    public AuthDto.ResponseLogin join(AuthDto.RequestJoin requestJoin) {
        verifiedRandomCode(requestJoin);
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

    private void verifiedRandomCode(AuthDto.RequestJoin requestJoin) {
        String randomCode = redisRepository.getData(requestJoin.getPhone())
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.NO_MATCH_PHONE_NUMBER));
        if (!randomCode.equals(requestJoin.getAuthNumber()))
            throw new BusinessLogicException(ErrorCode.NO_MATCH_AUTH_NUMBER);
    }

    public AuthDto.ResponseLogin login(AuthDto.RequestLogin requestLogin) {
        User dbUser = userService.findUserByEmail(requestLogin.getEmail());
        validatePassword(requestLogin, dbUser);
        return authMapper.toResponseToken(dbUser, jwtTokenProvider.createToken(dbUser));
    }

    private void validatePassword(AuthDto.RequestLogin requestLogin, User dbUser) {
        if (passwordService.isNotEqualPassword(requestLogin.getPassword(), dbUser.getPassword()))
            throw new BusinessLogicException(ErrorCode.NO_CORRECT_ACCOUNT);
    }

    public void duplicateEmail(String email) {
        if(userService.duplicateEmail(email))
            throw new BusinessLogicException(ErrorCode.EMAIL_ALREADY_EXIST);
    }
}
