package com.project.mything.auth.mapper;

import com.project.mything.auth.dto.AuthDto;
import com.project.mything.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.project.mything.util.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;


class AuthMapperTest {

    AuthMapper authMapper;

    @BeforeEach
    public void init() {
        authMapper = new AuthMapperImpl();
    }

    @Test
    @DisplayName("User 객체로 mapping 하는 메서드 테스트")
    public void toUser_suc() {
        //given
        //when
        User user = authMapper.toUser(REQUEST_JOIN);
        //then
        assertThat(user.getName()).isEqualTo(REQUEST_JOIN.getName());
        assertThat(user.getEmail()).isEqualTo(REQUEST_JOIN.getEmail());
        assertThat(user.getPhone()).isEqualTo(REQUEST_JOIN.getPhone());
        assertThat(user.getPassword()).isEqualTo(REQUEST_JOIN.getPassword());
        assertThat(user.getBirthday()).isEqualTo(REQUEST_JOIN.getBirthday());
    }

    @Test
    @DisplayName("ResponseLogin객체로 mapping 하는 테스트")
    public void toResponseToken_suc() {
        //given
        //when
        AuthDto.ResponseLogin responseLogin = authMapper.toResponseToken(ORIGINAL_USER, ACCESS_KEY);
        //then
        assertThat(responseLogin.getUserId()).isEqualTo(ORIGINAL_USER.getId());
        assertThat(responseLogin.getAccessToken()).isEqualTo(ACCESS_KEY);
    }

}