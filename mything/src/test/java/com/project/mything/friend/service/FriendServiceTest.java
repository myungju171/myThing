package com.project.mything.friend.service;

import com.project.mything.exception.BusinessLogicException;
import com.project.mything.exception.ErrorCode;
import com.project.mything.friend.dto.FriendDto;
import com.project.mything.friend.entity.Friend;
import com.project.mything.friend.mapper.FriendMapper;
import com.project.mything.friend.repository.FriendRepository;
import com.project.mything.item.entity.ItemUser;
import com.project.mything.user.dto.UserDto;
import com.project.mything.user.entity.Avatar;
import com.project.mything.user.entity.User;
import com.project.mything.user.mapper.UserMapper;
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
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.doNothing;

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
    @Mock
    UserMapper userMapper;

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
                .avatar(Avatar.builder().id(1L).remotePath("remotePath").build())
                .build();
        FriendDto.ResponseSimpleFriend responseSimpleFriend = FriendDto.ResponseSimpleFriend.builder()
                .userId(1L)
                .name("홍길동")
                .birthDay(LocalDate.of(1999, 4, 8))
                .itemCount(3)
                .avatarId(1L)
                .remotePath("remotePath")
                .build();
        given(userService.findUserWithItemUserByPhone(any())).willReturn(dbUser);
        given(friendMapper.toResponseFindUserResult(any(), any())).willReturn(responseSimpleFriend);
        //when
        FriendDto.ResponseSimpleFriend result = friendService.searchFriend(friendPhone);
        //then
        assertThat(result.getName()).isEqualTo(dbUser.getName());
        assertThat(result.getBirthDay()).isNotNull();
        assertThat(result.getInfoMessage()).isEqualTo(dbUser.getInfoMessage());
        assertThat(result.getItemCount()).isEqualTo(dbUser.getItemUserList().size());
        assertThat(result.getAvatarId()).isEqualTo(dbUser.getAvatar().getId());
        assertThat(result.getRemotePath()).isEqualTo(dbUser.getAvatar().getRemotePath());
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

    @Test
    @DisplayName("유저 친구 목록 조회시 성공 ")
    public void getFriends_suc() {
        //given
        User friendUser = User.builder()
                .id(2L)
                .name("friend")
                .avatar(Avatar.builder()
                        .id(1L)
                        .remotePath("remotePath")
                        .build())
                .build();

        ArrayList<Friend> friends = new ArrayList<>();

        User dbUser = User.builder()
                .id(1L)
                .name("test")
                .avatar(Avatar.builder()
                        .id(1L)
                        .remotePath("remotePath")
                        .build())
                .friendList(friends)
                .build();

        Friend friend = Friend.builder()
                .id(1L)
                .userFriendId(2L)
                .user(dbUser)
                .build();
        friends.add(friend);

        UserDto.ResponseSimpleUser responseSimpleUser = UserDto.ResponseSimpleUser.builder()
                .userId(1L)
                .name("test")
                .image("remotePath")
                .build();
        FriendDto.ResponseSimpleFriend responseSimpleFriend = FriendDto.ResponseSimpleFriend.builder()
                .userId(2L)
                .name("friend")
                .infoMessage("hello")
                .avatarId(1L)
                .remotePath("remotePath")
                .birthDay(LocalDate.of(1999, 4, 8))
                .itemCount(1)
                .build();

        FriendDto.RequestFriendList requestFriendList = FriendDto.RequestFriendList.builder()
                .userId(1L)
                .isBirthDay(false)
                .build();

        given(userService.findUserWithAvatar(any())).willReturn(dbUser);
        given(userService.findVerifiedUser(any())).willReturn(friendUser);
        given(friendMapper.toResponseFindUserResult(any(), any())).willReturn(responseSimpleFriend);
        given(userMapper.toResponseSimpleUser(any())).willReturn(responseSimpleUser);
        //when
        FriendDto.ResponseMultiFriend<FriendDto.ResponseSimpleFriend> result = friendService.getFriendInfo(requestFriendList);
        //then
        assertThat(result.getData().size()).isEqualTo(1);
        assertThat(result.getData().get(0).getUserId()).isEqualTo(2);
        assertThat(result.getData().get(0).getName()).isEqualTo("friend");
        assertThat(result.getData().get(0).getInfoMessage()).isEqualTo("hello");
        assertThat(result.getData().get(0).getItemCount()).isEqualTo(1);
        assertThat(result.getData().get(0).getBirthDay()).isEqualTo(LocalDate.of(1999, 4, 8));
        assertThat(result.getData().get(0).getAvatarId()).isEqualTo(1);
        assertThat(result.getData().get(0).getRemotePath()).isEqualTo("remotePath");
        assertThat(result.getUserInfo().getUserId()).isEqualTo(1L);
        assertThat(result.getUserInfo().getName()).isEqualTo("test");
        assertThat(result.getUserInfo().getImage()).isEqualTo("remotePath");
    }

    @Test
    @DisplayName("유저 친구 목록 조회시 친구가 없을시 빈배열 리턴 성공 ")
    public void getFriends_suc2() {
        //given

        User dbUser = User.builder()
                .id(1L)
                .name("test")
                .avatar(Avatar.builder()
                        .id(1L)
                        .remotePath("remotePath")
                        .build())
                .build();

        UserDto.ResponseSimpleUser responseSimpleUser = UserDto.ResponseSimpleUser.builder()
                .userId(1L)
                .name("test")
                .image("remotePath")
                .build();
        FriendDto.ResponseSimpleFriend responseSimpleFriend = FriendDto.ResponseSimpleFriend.builder()
                .userId(2L)
                .name("friend")
                .infoMessage("hello")
                .avatarId(1L)
                .remotePath("remotePath")
                .birthDay(LocalDate.of(1999, 4, 8))
                .itemCount(1)
                .build();

        FriendDto.RequestFriendList requestFriendList = FriendDto.RequestFriendList.builder()
                .userId(1L)
                .isBirthDay(false)
                .build();

        given(userService.findUserWithAvatar(any())).willReturn(dbUser);
        given(userMapper.toResponseSimpleUser(any())).willReturn(responseSimpleUser);
        //when
        FriendDto.ResponseMultiFriend<FriendDto.ResponseSimpleFriend> result = friendService.getFriendInfo(requestFriendList);
        //then
        assertThat(result.getData().size()).isEqualTo(0);
        assertThat(result.getUserInfo().getUserId()).isEqualTo(1L);
        assertThat(result.getUserInfo().getName()).isEqualTo("test");
        assertThat(result.getUserInfo().getImage()).isEqualTo("remotePath");
    }

    @Test
    @DisplayName("유저 친구 목록 조회시 존재하지 않는 유저일시 404 리턴")
    public void getFriends_fail() {
        //given
        FriendDto.RequestFriendList requestFriendList = FriendDto.RequestFriendList.builder()
                .userId(1L)
                .isBirthDay(false)
                .build();

        given(userService.findUserWithAvatar(any())).willThrow(new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
        //when
        //then
        assertThatThrownBy(() -> friendService.getFriendInfo(requestFriendList)).isInstanceOf(BusinessLogicException.class);
    }

}