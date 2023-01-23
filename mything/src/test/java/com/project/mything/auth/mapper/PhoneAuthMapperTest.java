package com.project.mything.auth.mapper;

import com.project.mything.auth.dto.PhoneAuthDto;
import com.project.mything.auth.entity.PhoneAuth;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class PhoneAuthMapperTest {

    PhoneAuthMapper phoneAuthMapper;

    @BeforeEach
    public void init() {
        phoneAuthMapper = new PhoneAuthMapperImpl();
    }

    @Test
    @DisplayName("PhoneAuth 객체로 mapping하는 메서드 테스트")

    public void toPhoneAuth_suc() {
        //given
        PhoneAuthDto.RequestAuthNumber requestAuthNumber = PhoneAuthDto.RequestAuthNumber.builder()
                .phone("01011112222")
                .build();
        String authNumber = "1234";
        //when
        PhoneAuth phoneAuth = phoneAuthMapper.toPhoneAuth(requestAuthNumber, authNumber);
        //then
        assertThat(phoneAuth.getPhone()).isEqualTo(requestAuthNumber.getPhone());
        assertThat(phoneAuth.getAuthNumber()).isEqualTo(authNumber);
    }

}