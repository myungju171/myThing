package com.project.mything.user.service;

import com.project.mything.exception.BusinessLogicException;
import com.project.mything.exception.ErrorCode;
import com.project.mything.user.dto.UserDto;
import com.project.mything.user.entity.User;
import com.project.mything.user.mapper.UserMapper;
import com.project.mything.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.project.mything.util.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith({MockitoExtension.class})
class UserServiceTest {

    @InjectMocks
    UserService userService;
    @Mock
    UserRepository userRepository;
    @Mock
    UserMapper userMapper;

    @Test
    @DisplayName("findVerifiedUser메서드 테스트 통과 ")
    public void findVerifiedUser_suc() {
        //given
        given(userRepository.findById(any())).willReturn(Optional.of(ORIGINAL_USER));
        //when
        User result = userService.findVerifiedUser(1L);
        //then
        assertThat(result.getId()).isEqualTo(ORIGINAL_USER.getId());
    }

    @Test
    @DisplayName("findVerifiedUser메서드 실패시 USER_NOT_FOUND 404리턴 ")
    public void findVerifiedUser_fail() {
        //given
        given(userRepository.findById(any()))
                .willThrow(new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
        //when
        //then
        assertThatThrownBy(() -> userService.findVerifiedUser(1L))
                .isInstanceOf(BusinessLogicException.class);
    }

    @Test
    @DisplayName("getUserInfo메서드 성공")
    public void getUserDetail_suc() {
        //given
        given(userRepository.findUserWithImage(any())).willReturn(Optional.of(ORIGINAL_USER));
        given(userMapper.toResponseDetailUser(any())).willReturn(RESPONSE_DETAIL_USER);
        //when
        UserDto.ResponseDetailUser userInfo = userService.getUserDetail(USER_INFO);
        //then
        assertThat(userInfo.getUserId()).isEqualTo(ID1);
        assertThat(userInfo.getName()).isEqualTo(NAME);
        assertThat(userInfo.getPhone()).isEqualTo(PHONE);
        assertThat(userInfo.getBirthday().toString()).isEqualTo(BIRTHDAY.toString());
        assertThat(userInfo.getInfoMessage()).isEqualTo(INFO_MESSAGE);
        assertThat(userInfo.getAvatar().getImageId()).isEqualTo(ID1);
        assertThat(userInfo.getAvatar().getRemotePath()).isEqualTo(REMOTE_PATH);
    }

    @Test
    @DisplayName("getUserDetail메서드 존재하지 않는 유저 실패")
    public void getUserDetail_fail() {
        //given
        given(userRepository.findUserWithImage(any()))
                .willThrow(new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
        //when
        //then
        assertThatThrownBy(() -> userService.findUserWithAvatar(any())).isInstanceOf(BusinessLogicException.class);
    }
}
