package com.project.mything.user.repository;

import com.project.mything.user.entity.Avatar;
import com.project.mything.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AvatarRepositoryTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    AvatarRepository avatarRepository;

    @Test
    @DisplayName("아바타 저장시 유저 매핑 테스트")
    public void UserAndAvatarCaseCadeCheck() throws Exception {
    //given
        User user = User.builder()
                .name("홍길동")
                .build();
        User dbUser = userRepository.save(user);
        Avatar avatar = Avatar.builder()
                .user(user)
                .build();
        avatar.getUser().addAvatar(avatar);

        //when
        Avatar dbAvatar = avatarRepository.save(avatar);
        User user1 = userRepository.findById(dbUser.getId()).orElseThrow(() -> new Exception("sdf"));
        //then
        assertThat(dbAvatar.getUser().getName()).isEqualTo(user.getName());
        assertThat(user1.getAvatar().getId()).isEqualTo(dbAvatar.getId());
    }
}