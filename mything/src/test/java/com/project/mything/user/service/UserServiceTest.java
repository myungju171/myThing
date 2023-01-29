package com.project.mything.user.service;

import com.project.mything.exception.BusinessLogicException;
import com.project.mything.exception.ErrorCode;
import com.project.mything.user.entity.User;
import com.project.mything.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
}
