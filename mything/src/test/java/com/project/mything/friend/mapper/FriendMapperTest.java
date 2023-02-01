package com.project.mything.friend.mapper;

import com.project.mything.friend.dto.FriendDto;
import com.project.mything.user.entity.Avatar;
import com.project.mything.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class FriendMapperTest {

    private FriendMapper friendMapper;

    @BeforeEach
    public void init() {

        friendMapper = new FriendMapperImpl();
    }

    @Test
    @DisplayName("toResponseFindUserResult 매칭 테스트")
    public void toResponseFindUserResult_suc(){
    //given
        User dbUser = User.builder()
                .id(1L)
                .name("홍길동")
                .infoMessage("hello")
                .birthDay(LocalDate.of(1999, 4, 8))
                .avatar(Avatar.builder().id(1L).remotePath("remotePath").build())
                .build();
        Integer itemCount = 3;
        //when
        FriendDto.ResponseSimpleFriend result = friendMapper.toResponseFindUserResult(dbUser, itemCount);
        //then
        assertThat(result.getUserId()).isEqualTo(dbUser.getId());
        assertThat(result.getName()).isEqualTo(dbUser.getName());
        assertThat(result.getInfoMessage()).isEqualTo(dbUser.getInfoMessage());
        assertThat(result.getBirthDay()).isEqualTo(dbUser.getBirthDay());
        assertThat(result.getAvatarId()).isEqualTo(dbUser.getAvatar().getId());
        assertThat(result.getRemotePath()).isEqualTo(dbUser.getAvatar().getRemotePath());
    }
}