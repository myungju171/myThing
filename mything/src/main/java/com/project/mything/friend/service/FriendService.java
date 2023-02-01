package com.project.mything.friend.service;

import com.project.mything.friend.dto.FriendDto;
import com.project.mything.friend.entity.Friend;
import com.project.mything.friend.mapper.FriendMapper;
import com.project.mything.friend.repository.FriendRepository;
import com.project.mything.user.dto.UserDto;
import com.project.mything.user.entity.User;
import com.project.mything.user.mapper.UserMapper;
import com.project.mything.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
    private final FriendMapper friendMapper;
    private final UserService userService;
    private final UserMapper userMapper;

    public FriendDto.ResponseSimpleFriend searchFriend(String friendPhone) {
        User dbFriend
                = userService.findUserWithItemUserByPhone(friendPhone);

        Integer ItemCountOfDbFriend = dbFriend.getItemUserList().size();
        return friendMapper.toResponseFindUserResult(dbFriend, ItemCountOfDbFriend);
    }

    public FriendDto.ResponseMultiFriend<FriendDto.ResponseSimpleFriend> getFriendInfo(FriendDto.RequestFriendList requestFriendList) {
        User dbUser = userService.findUserWithAvatar(requestFriendList.getUserId());

        List<FriendDto.ResponseSimpleFriend> responseSimpleFriends
                = makeSimpleFriendDtoList(requestFriendList.getIsBirthDay(), dbUser);

        return  new FriendDto.ResponseMultiFriend<FriendDto.ResponseSimpleFriend>(responseSimpleFriends, getUserInfoDto(dbUser));

    }

    private UserDto.ResponseSimpleUser getUserInfoDto(User dbUser) {
        return userMapper.toResponseSimpleUser(dbUser);
    }

    private List<FriendDto.ResponseSimpleFriend> makeSimpleFriendDtoList(Boolean birthDay, User dbUser) {
        List<FriendDto.ResponseSimpleFriend> simpleFriendDtoList = new ArrayList<FriendDto.ResponseSimpleFriend>();

        for (int i = 0; i < dbUser.getFriendList().size(); i++) {
            Friend friend = dbUser.getFriendList().get(i);
            User userFriend = userService.findVerifiedUser(friend.getUserFriendId());
            if (birthDay) {
                distinguishBirthDayUser(simpleFriendDtoList, userFriend);
            } else {
                addFriendInfoToResult(simpleFriendDtoList, userFriend);
            }
        }
        return simpleFriendDtoList;
    }

    private void distinguishBirthDayUser(List<FriendDto.ResponseSimpleFriend> simpleFriendDtoList, User userFriend) {
        LocalDate now = LocalDate.now();

        boolean month = Objects.equals(userFriend.getBirthDay().getMonth(), now.getMonth());
        boolean day = userFriend.getBirthDay().getDayOfMonth() == now.getDayOfMonth();
        if ( day && month) {
            addFriendInfoToResult(simpleFriendDtoList, userFriend);
        }
    }

    private void addFriendInfoToResult(List<FriendDto.ResponseSimpleFriend> simpleFriendDtoList, User userFriend) {
        simpleFriendDtoList.add(friendMapper.toResponseFindUserResult(userFriend, userFriend.getItemUserList().size()));
    }

}
