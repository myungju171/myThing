package com.project.mything.friend.mapper;

import com.project.mything.friend.dto.FriendDto;
import com.project.mything.friend.entity.Friend;
import com.project.mything.friend.entity.enums.FriendStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.project.mything.util.TestConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

class FriendMapperTest {

    private FriendMapper friendMapper;

    @BeforeEach
    public void init() {

        friendMapper = new FriendMapperImpl();
    }

    @Test
    @DisplayName("toResponseFindUserResult 매핑 테스트")
    public void toResponseFindUserResult_suc() {
        //given
        //when
        FriendDto.ResponseSimpleFriend result = friendMapper.toResponseSimpleFriend(RESPONSE_DETAIL_USER, ITEM_COUNT);
        //then
        assertThat(result.getUser().getUserId()).isEqualTo(RESPONSE_DETAIL_USER.getUserId());
        assertThat(result.getUser().getName()).isEqualTo(RESPONSE_DETAIL_USER.getName());
        assertThat(result.getUser().getInfoMessage()).isEqualTo(RESPONSE_DETAIL_USER.getInfoMessage());
        assertThat(result.getUser().getBirthday()).isEqualTo(RESPONSE_DETAIL_USER.getBirthday());
        assertThat(result.getUser().getAvatar().getImageId()).isEqualTo(RESPONSE_DETAIL_USER.getAvatar().getImageId());
        assertThat(result.getUser().getAvatar().getRemotePath()).isEqualTo(RESPONSE_DETAIL_USER.getAvatar().getRemotePath());
        assertThat(result.getItemCount()).isEqualTo(ITEM_COUNT);
    }

    @Test
    @DisplayName("toFriend 매핑 테스트")
    public void toFriend_suc() {
        //given
        //when
        Friend result = friendMapper.toFriend(ORIGINAL_USER, DIFF_ORIGINAL_USER);
        //then
        assertThat(result.getUser()).isEqualTo(ORIGINAL_USER);
        assertThat(result.getUserFriend()).isEqualTo(DIFF_ORIGINAL_USER);
        assertThat(result.getFriendStatus()).isEqualTo(FriendStatus.ACTIVE);
    }
}
