package com.project.mything.user.service;

import com.project.mything.exception.BusinessLogicException;
import com.project.mything.exception.ErrorCode;
import com.project.mything.user.dto.UserDto;
import com.project.mything.user.entity.Avatar;
import com.project.mything.user.entity.User;
import com.project.mything.user.mapper.UserMapper;
import com.project.mything.user.repository.UserRepository;
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
        User dbUser = User.builder()
                .id(1L)
                .build();
        given(userRepository.findById(any())).willReturn(Optional.of(dbUser));
        //when
        User result = userService.findVerifiedUser(1L);
        //then
        assertThat(result.getId()).isEqualTo(dbUser.getId());
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
    public void getUserInfo_suc() {
        //given
        User dbUser = User.builder()
                .id(1L)
                .name("test")
                .birthDay(LocalDate.of(1999, 4, 8))
                .infoMessage("testInfo")
                .phone("01012345678")
                .avatar(Avatar.builder().id(1L).remotePath("remotePath").build())
                .build();

        UserDto.ResponseDetailUser result = UserDto.ResponseDetailUser.builder()
                .userId(1L)
                .name("test")
                .birthDay(LocalDate.of(1999, 4, 8))
                .infoMessage("testInfo")
                .phone("01012345678")
                .avatarId(1L)
                .image("remotePath")
                .build();

        given(userRepository.findUserWithAvatar(any())).willReturn(Optional.of(dbUser));
        given(userMapper.toResponseDetailUser(any(),any())).willReturn(result);
        //when
        UserDto.ResponseDetailUser userInfo = userService.getUserInfo(dbUser.getId());
        //then
        assertThat(userInfo.getUserId()).isEqualTo(dbUser.getId());
        assertThat(userInfo.getPhone()).isEqualTo(dbUser.getPhone());
        assertThat(userInfo.getBirthDay().compareTo(dbUser.getBirthDay())).isEqualTo(0);
        assertThat(userInfo.getInfoMessage()).isEqualTo(dbUser.getInfoMessage());
        assertThat(userInfo.getAvatarId()).isEqualTo(dbUser.getAvatar().getId());
        assertThat(userInfo.getImage()).isEqualTo(dbUser.getAvatar().getRemotePath());
    }

    @Test
    @DisplayName("getUserInfo메서드 존재하지 않는 유저 실패")
    public void getUserInfo_fail() {
        //given

        given(userRepository.findUserWithAvatar(any()))
                .willThrow(new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
        //when
        //then
        assertThatThrownBy(() -> userService.getUserInfo(any())).isInstanceOf(RuntimeException.class);
    }
}
