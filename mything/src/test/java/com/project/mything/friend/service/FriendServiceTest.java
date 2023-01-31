package com.project.mything.friend.service;

import com.project.mything.exception.BusinessLogicException;
import com.project.mything.exception.ErrorCode;
import com.project.mything.friend.dto.FriendDto;
import com.project.mything.friend.mapper.FriendMapper;
import com.project.mything.friend.repository.FriendRepository;
import com.project.mything.item.entity.ItemUser;
import com.project.mything.user.entity.User;
import com.project.mything.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class FriendServiceTest {

    @InjectMocks
    FriendService friendService;
    @Mock
    FriendRepository friendRepository;
    @Mock
    FriendMapper friendMapper;
    @Mock
    UserService userService;

    @Test
    @DisplayName("핸드폰 번호 유저 조회시 성공 ")
    public void searchFriend_suc() {
        //given
        String friendPhone = "01012345678";
        List<ItemUser> itemUserList = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            itemUserList.add(ItemUser.builder().id((long) i)
                    .build());
        }
        User dbUser = User.builder()
                .id(1L)
                .name("홍길동")
                .birthDay(LocalDate.of(1999, 4, 8))
                .itemUserList(itemUserList)
                .build();

        FriendDto.ResponseFindUserResult responseFindUserResult = FriendDto.ResponseFindUserResult.builder()
                .userId(1L)
                .name("홍길동")
                .birthDay(LocalDate.of(1999, 4, 8))
                .itemCount(3)
                .build();
        given(userService.findUserWithItemUserByPhone(any())).willReturn(dbUser);
        given(friendMapper.toResponseFindUserResult(any(), any())).willReturn(responseFindUserResult);
        //when
        FriendDto.ResponseFindUserResult result = friendService.searchFriend(friendPhone);
        //then
        assertThat(result.getName()).isEqualTo(dbUser.getName());
        assertThat(result.getBirthDay()).isNotNull();
        assertThat(result.getInfoMessage()).isEqualTo(dbUser.getInfoMessage());
        assertThat(result.getItemCount()).isEqualTo(dbUser.getItemUserList().size());
    }

    @Test
    @DisplayName("핸드폰 번호 유저 조회시 존재하지 않는 핸드폰 번호 실패 404 ")
    public void searchFriend_fail() {
        //given
        String friendPhone = "01012345678";

        given(userService.findUserWithItemUserByPhone(any()))
                .willThrow(new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
        //when
        //then
        assertThatThrownBy(() -> friendService.searchFriend(friendPhone)).isInstanceOf(BusinessLogicException.class);
    }
}