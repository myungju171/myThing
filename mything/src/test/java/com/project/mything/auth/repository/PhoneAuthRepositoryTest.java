package com.project.mything.auth.repository;

import com.project.mything.auth.entity.PhoneAuth;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class PhoneAuthRepositoryTest {

    @Autowired
    PhoneAuthRepository phoneAuthRepository;


    @Test
    @DisplayName("해당 핸드폰 번호를 가진 데이터가 있으면 해당 PhoneAuth 객체를 가져온다.")
    public void findPhoneAuthByPhone_suc(){
    //given
        PhoneAuth phoneAuth = PhoneAuth.builder()
                .phone("01011112222")
                .authNumber("1234")
                .build();

        PhoneAuth dbPhoneAuth = phoneAuthRepository.save(phoneAuth);

        //when
        PhoneAuth foundPhoneAuth = phoneAuthRepository.findPhoneAuthByPhone("01011112222")
                .orElseThrow(() -> new RuntimeException());

        //then
        assertThat(foundPhoneAuth.getPhone()).isEqualTo(dbPhoneAuth.getPhone());
        assertThat(foundPhoneAuth.getAuthNumber()).isEqualTo(dbPhoneAuth.getAuthNumber());
        assertThat(foundPhoneAuth.getId()).isEqualTo(dbPhoneAuth.getId());
    }

    @Test
    @DisplayName("해당 핸드폰 번호를 가진 데이터가 없을때 예외를 던진다.")
    public void findPhoneAuthByPhone_fail1(){
    //given
        String noneSavePhone = "01012345678";
    //when
    //then
        assertThatThrownBy(() -> phoneAuthRepository
                .findPhoneAuthByPhone(noneSavePhone)
                .orElseThrow(() -> new RuntimeException()))
                .isInstanceOf(RuntimeException.class);
    }
}