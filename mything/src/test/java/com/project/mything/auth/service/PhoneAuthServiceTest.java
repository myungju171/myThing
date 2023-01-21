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
import net.nurigo.sdk.message.model.Message;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PhoneAuthServiceTest {

    @InjectMocks
    PhoneAuthService phoneAuthService;
    @Mock
    PhoneAuthRepository phoneAuthRepository;
    @Mock
    PhoneAuthMapper phoneAuthMapper;
    @Mock
    SendService sendService;
    @Mock
    UserRepository userRepository;
    @Mock
    UserMapper userMapper;

    @Test
    @DisplayName("인증번호 전송 서비스로직 성공 테스트 ")
    public void sendAuthNumber_suc() {
        //given
        PhoneAuthDto.RequestAuthNumber requestAuthNumber = PhoneAuthDto.RequestAuthNumber.builder()
                .phone("01011112222")
                .build();

        PhoneAuth phoneAuth = PhoneAuth.builder()
                .id(1L)
                .authNumber("1234")
                .phone("01011112222")
                .build();
        given(phoneAuthMapper.toPhoneAuth(any(), any())).willReturn(phoneAuth);
        given(sendService.send(any(), any())).willReturn(new Message());
        given(phoneAuthRepository.save(any())).willReturn(phoneAuth);
        //when
        String result = phoneAuthService.sendAuthNumber(requestAuthNumber);
        //then
        assertThat(result).isEqualTo("true");
    }

    @Test
    @DisplayName("11자리의 휴대폰 번호가 아니라면 Bad_Request 예외를 던진다.")
    public void sendAuthNumber_fail() {
        //given
        PhoneAuthDto.RequestAuthNumber requestAuthNumber = PhoneAuthDto.RequestAuthNumber.builder()
                .phone("010111")
                .build();
        //when
        //then
        assertThatThrownBy(() -> phoneAuthService.sendAuthNumber(requestAuthNumber)).isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("회원가입 서비스로직 성공 테스트")
    public void join_suc() {
        //given
        PhoneAuthDto.RequestJoin requestJoin = PhoneAuthDto.RequestJoin.builder()
                .phone("01011112222")
                .authNumber("1234")
                .build();

        PhoneAuth phoneAuth = PhoneAuth.builder()
                .phone("01011112222")
                .id(1L)
                .authNumber("1234")
                .build();

        User user = User.builder()
                .phone("01011112222")
                .id(1L)
                .birthDay(LocalDate.of(1999, 11, 11))
                .name("홍길동")
                .build();

        UserDto.ResponseUserId responseUserId = UserDto.ResponseUserId.builder()
                .userId(1L)
                .build();

        given(phoneAuthRepository.findPhoneAuthByPhone(requestJoin.getPhone())).willReturn(Optional.of(phoneAuth));
        given(userRepository.findUserByPhone(any())).willReturn(Optional.empty());
        given(phoneAuthMapper.toUser(requestJoin)).willReturn(user);
        given(userMapper.toResponseUserId(any())).willReturn(responseUserId);

        //when
        UserDto.ResponseUserId join = phoneAuthService.join(requestJoin);
        //then
        verify(phoneAuthRepository, times(1)).delete(any());
        verify(phoneAuthRepository, times(1)).findPhoneAuthByPhone(any());
        verify(userRepository, times(1)).findUserByPhone(any());
        assertThat(join.getUserId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("회원가입시 이미 회원가입 했었던 유저는 기존 회원아이디를 리턴한다. ")
    public void join_suc2() {
        //given
        PhoneAuthDto.RequestJoin requestJoin = PhoneAuthDto.RequestJoin.builder()
                .phone("01011112222")
                .authNumber("1234")
                .build();

        PhoneAuth phoneAuth = PhoneAuth.builder()
                .phone("01011112222")
                .id(1L)
                .authNumber("1234")
                .build();

        User user = User.builder()
                .phone("01011112222")
                .id(2L)
                .birthDay(LocalDate.of(1999, 11, 11))
                .name("홍길동")
                .build();

        UserDto.ResponseUserId responseUserId = UserDto.ResponseUserId.builder()
                .userId(2L)
                .build();
        given(phoneAuthRepository.findPhoneAuthByPhone(requestJoin.getPhone())).willReturn(Optional.of(phoneAuth));
        given(userRepository.findUserByPhone(any())).willReturn(Optional.of(user));
        given(userMapper.toResponseUserId(any())).willReturn(responseUserId);
        //when
        UserDto.ResponseUserId join = phoneAuthService.join(requestJoin);
        //then
        verify(phoneAuthRepository, times(1)).delete(any());
        verify(userRepository, times(1)).findUserByPhone(any());
        assertThat(join.getUserId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("해당 핸드폰번호로 인증받은 기록이 없다면 NO_MATCH_PHONE_NUMBER 404을 리턴한다. ")
    public void join_fail1() {
        //given
        PhoneAuthDto.RequestJoin requestJoin = PhoneAuthDto.RequestJoin.builder()
                .phone("01011112222")
                .authNumber("1234")
                .build();

        given(phoneAuthRepository.findPhoneAuthByPhone(any()))
                .willThrow(new BusinessLogicException(ErrorCode.NO_MATCH_PHONE_NUMBER));
        //when
        //then
        assertThatThrownBy(() -> phoneAuthService.join(requestJoin)).isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("해당 핸드폰번호로 인증받은 인증번호가 다르다면 NO_MATCH_AUTH_NUMBER 404을 리턴한다. ")
    public void join_fail2() {
        //given
        PhoneAuthDto.RequestJoin requestJoin = PhoneAuthDto.RequestJoin.builder()
                .phone("01011112222")
                .authNumber("1234")
                .build();

        PhoneAuth phoneAuth = PhoneAuth.builder()
                .phone("01011112222")
                .id(1L)
                .authNumber("4567")
                .build();

        given(phoneAuthRepository.findPhoneAuthByPhone(any())).willReturn(Optional.of(phoneAuth));


        //when
        //then
        assertThatThrownBy(() -> phoneAuthService.join(requestJoin)).isInstanceOf(BusinessLogicException.class);
    }


}