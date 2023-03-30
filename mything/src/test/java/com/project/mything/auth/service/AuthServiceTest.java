package com.project.mything.auth.service;

import com.project.mything.auth.dto.AuthDto;
import com.project.mything.auth.mapper.AuthMapper;
import com.project.mything.redis.repository.RedisRepository;
import com.project.mything.exception.BusinessLogicException;
import com.project.mything.exception.ErrorCode;
import com.project.mything.user.service.UserService;
import com.project.mything.security.jwt.service.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.project.mything.util.TestConstants.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    AuthService authService;

    @Mock
    AuthMapper authMapper;
    @Mock
    AuthNumSendService authNumSendService;
    @Mock
    PasswordService passwordService;
    @Mock
    JwtTokenProvider jwtTokenProvider;
    @Mock
    RedisRepository redisRepository;
    @Mock
    UserService userService;

    @Test
    @DisplayName("인증번호 전송 서비스로직 성공 테스트 ")
    public void sendAuthNumber_suc() {
        //given
        given(passwordService.getRandomCode()).willReturn(AUTH_NUMBER);
        given(redisRepository.saveData(any(), any(), any())).willReturn(true);
        //when
        Boolean result = authService.sendAuthNumber(REQUEST_AUTH_NUMBER);
        //then
        assertThat(result).isTrue();
        verify(authNumSendService, times(1)).send(any(), any());
    }

    @Test
    @DisplayName("회원가입 서비스로직 성공 테스트")
    public void join_suc() {
        //given
        given(redisRepository.getData(any())).willReturn(Optional.of(AUTH_NUMBER));
        given(authMapper.toUser(any())).willReturn(ORIGINAL_USER);
        given(userService.saveUser(any())).willReturn(ORIGINAL_USER);
        given(jwtTokenProvider.createToken(any())).willReturn(ACCESS_KEY);
        given(authMapper.toResponseToken(any(), any())).willReturn(RESPONSE_LOGIN);
        //when
        AuthDto.ResponseLogin result = authService.join(REQUEST_JOIN);
        //then
        assertThat(result.getAccessToken()).isEqualTo(ACCESS_KEY);
        assertThat(result.getUserId()).isEqualTo(ID1);
        verify(passwordService, times(1)).encodePassword(any());

    }

    @Test
    @DisplayName("해당 핸드폰번호로 인증받은 기록이 없다면 NO_MATCH_PHONE_NUMBER 404을 리턴한다.")
    public void join_fail1() {
        //given
        given(redisRepository.getData(any())).willThrow(new BusinessLogicException(ErrorCode.NO_MATCH_PHONE_NUMBER));
        //when
        //then
        assertThatThrownBy(() -> authService.join(REQUEST_JOIN)).isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("해당 핸드폰번호로 인증받은 인증번호가 다르다면 NO_MATCH_AUTH_NUMBER 404을 리턴한다.")
    public void join_fail2() {
        //given
        given(redisRepository.getData(any())).willReturn(Optional.of(DIFF_AUTH_NUMBER));
        //when
        //then
        assertThatThrownBy(() -> authService.join(REQUEST_JOIN)).isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("로그인 서비스로직 성공 테스트")
    public void login_suc1() {
        //given
        given(userService.findUserByEmail(any())).willReturn(ORIGINAL_USER);
        given(jwtTokenProvider.createToken(any())).willReturn(ACCESS_KEY);
        given(authMapper.toResponseToken(any(), any())).willReturn(RESPONSE_LOGIN);
        //when
        AuthDto.ResponseLogin result = authService.login(REQUEST_LOGIN);
        //then
        assertThat(result.getUserId()).isEqualTo(ID1);
        assertThat(result.getAccessToken()).isEqualTo(ACCESS_KEY);
    }

    @Test
    @DisplayName("로그인시 해당 email이 존재하지 않을경우 409 리턴")
    public void login_fail1() {
        //given
        given(userService.findUserByEmail(any())).willThrow(new BusinessLogicException(ErrorCode.NO_CORRECT_ACCOUNT));
        //when
        //then
        assertThatThrownBy(() -> authService.login(REQUEST_LOGIN)).isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("로그인시 비밀번호가 다를 경우 409 리턴")
    public void login_fail2() {
        //given
        given(userService.findUserByEmail(any())).willThrow(new BusinessLogicException(ErrorCode.NO_CORRECT_ACCOUNT));
        //when
        //then
        assertThatThrownBy(() -> authService.login(REQUEST_LOGIN)).isInstanceOf(BusinessLogicException.class);
    }
}