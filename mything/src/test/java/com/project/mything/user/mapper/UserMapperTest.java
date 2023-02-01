package com.project.mything.user.mapper;

import com.project.mything.user.dto.UserDto;
import com.project.mything.user.entity.Avatar;
import com.project.mything.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    UserMapper userMapper;

    @BeforeEach
    public void init() {
        this.userMapper = new UserMapperImpl();
    }

    @Test
    @DisplayName("User 객체에서 toResponseUserId 객체로 매핑 테스트")
    public void toResponseUserId_suc(){
    //given
        User user = User.builder()
                .id(1L)
                .build();
        //when
        UserDto.ResponseUserId result = userMapper.toResponseUserId(user);
        //then
        assertThat(result.getUserId()).isEqualTo(user.getId());
    }

    @Test
    @DisplayName("User 객체에서 toResponseSimpleUser 객체로 매핑 테스트")
    public void toResponseSimpleUser_suc(){
    //given
        Avatar avatar = Avatar.builder()
                .remotePath("remotePath")
                .build();
        User user = User.builder()
                .id(1L)
                .name("홍길동")
                .avatar(avatar)
                .build();
        //when
        UserDto.ResponseSimpleUser result = userMapper.toResponseSimpleUser(user);
        //then
        assertThat(result.getUserId()).isEqualTo(user.getId());
        assertThat(result.getName()).isEqualTo(user.getName());
        assertThat(result.getImage()).isEqualTo(user.getAvatar().getRemotePath());
    }

    @Test
    @DisplayName("User에서 ResponseImageURl 객체로 매핑 테스트")
    public void toResponseImageUrl_suc(){
    //given
        User dbUser = User.builder()
                .id(1L)
                .avatar(Avatar.builder().id(2L).remotePath("remotePath").build())
                .build();
        //when
        UserDto.ResponseImageURl responseImageURl = userMapper.toResponseImageUrl(dbUser);
        //then
        assertThat(responseImageURl.getUserId()).isEqualTo(dbUser.getId());
        assertThat(responseImageURl.getAvatarId()).isEqualTo(dbUser.getAvatar().getId());
        assertThat(responseImageURl.getRemotePath()).isEqualTo(dbUser.getAvatar().getRemotePath());

    }
}