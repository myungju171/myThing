package com.project.mything.util;

import com.project.mything.auth.dto.AuthDto;
import com.project.mything.user.entity.User;

import java.time.LocalDate;

public class TestConstants {

    public final static Long ID1 = 1L;
    public final static Long Id2 = 2L;
    public final static String EMAIL = "test@naver.com";
    public final static String DIFF_EMAIL = "test1@naver.com";
    public final static String PASSWORD = "testPassword1!";
    public final static String DIFF_PASSWORD = "testPassword2@";
    public final static String NAME = "test";
    public final static String PHONE = "01012345678";
    public final static String DIFF_PHONE = "01087654321";
    public final static String INVALID_PHONE = "1234567890123";
    public final static LocalDate BIRTHDAY = LocalDate.of(2023, 3, 20);
    public final static LocalDate DIFF_BIRTHDAY = LocalDate.of(2023, 3, 20);
    public final static String AUTH_NUMBER = "1234";
    public final static String DIFF_AUTH_NUMBER = "4321";
    public final static String ACCESS_KEY = "accessKey";
    public final static String INVALID_ACCESS_KEY = "invalidAccessKey";
    public final static AuthDto.RequestJoin REQUEST_JOIN = AuthDto.RequestJoin.builder()
            .email(EMAIL)
            .name(NAME)
            .phone(PHONE)
            .password(PASSWORD)
            .birthday(BIRTHDAY)
            .authNumber(AUTH_NUMBER).build();

    public final static AuthDto.RequestAuthNumber REQUEST_AUTH_NUMBER = AuthDto.RequestAuthNumber.builder()
            .phone(PHONE)
            .build();

    public final static AuthDto.ResponseLogin RESPONSE_LOGIN = AuthDto.ResponseLogin.builder()
            .userId(ID1)
            .accessToken(ACCESS_KEY)
            .build();

    public final static User ORIGINAL_USER = User.builder()
            .id(ID1)
            .name(NAME)
            .phone(PHONE)
            .birthday(BIRTHDAY)
            .email(EMAIL)
            .password(PASSWORD)
            .build();

    public final static User REQUEST_USER = User.builder()
            .name(NAME)
            .phone(PHONE)
            .birthday(BIRTHDAY)
            .email(EMAIL)
            .password(PASSWORD)
            .build();
    public static final AuthDto.RequestLogin REQUEST_LOGIN = AuthDto.RequestLogin.builder()
            .email(EMAIL)
            .password(PASSWORD)
            .build();
}
