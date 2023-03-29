package com.project.mything.user.repository;

import com.project.mything.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("핸드폰 번호로 유저를 찾아오는 메서드 테스트")
    public void findUserByPhone_suc() {
        //given
        User user = User.builder()
                .phone("01012345678")
                .build();
        User dbUser = userRepository.save(user);
        //when
        User result = userRepository.findUserByPhone("01012345678")
                .orElseThrow(RuntimeException::new);
        //then
        assertThat(result.getPhone()).isEqualTo(user.getPhone());
    }

    @Test
    @DisplayName("핸드폰 번호로 유저와 아이템유저 객체를 한번에 조회 ")
    public void findUserWithItemUserByPhone_suc() {
        //given
        User user = User.builder()
                .phone("01012345678")
                .build();
        User dbUser = userRepository.save(user);
        //when
        User result = userRepository.findUserWithItemUserByPhone("01012345678")
                .orElseThrow(RuntimeException::new);
        //then
        assertThat(result.getPhone()).isEqualTo(user.getPhone());
    }

    @Test
    @DisplayName("유저 아이디로 유저와 아바타 객체를 한번에 조회")
    public void findUserWithAvatar_suc() {
        //given
        User user = User.builder()
                .build();
        User dbUser = userRepository.save(user);
        //when
        User result = userRepository.findUserWithImage(user.getId())
                .orElseThrow(RuntimeException::new);
        //then
        assertThat(result.getId()).isEqualTo(user.getId());

    }

}