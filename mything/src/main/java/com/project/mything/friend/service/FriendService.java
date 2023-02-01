package com.project.mything.friend.service;

import com.project.mything.friend.dto.FriendDto;
import com.project.mything.friend.mapper.FriendMapper;
import com.project.mything.friend.repository.FriendRepository;
import com.project.mything.user.dto.UserDto;
import com.project.mything.user.entity.User;
import com.project.mything.user.mapper.UserMapper;
import com.project.mything.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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

    public FriendDto.ResponseMultiFriend<FriendDto.ResponseSimpleFriend> getFriends(Long userId) {
        User dbUser = userService.findUserWithAvatar(userId);

        List<FriendDto.ResponseSimpleFriend> data = getResponseSimpleFriendList(dbUser);

        UserDto.ResponseSimpleUser userInfo = userMapper.toResponseSimpleUser(dbUser);

        return new FriendDto.ResponseMultiFriend<FriendDto.ResponseSimpleFriend>(data, userInfo);
    }

    private List<FriendDto.ResponseSimpleFriend> getResponseSimpleFriendList(User dbUser) {
        return dbUser.getFriendList().stream().map(friend -> {
            User userFriend = userService.findVerifiedUser(friend.getUserFriendId());
            return friendMapper.toResponseFindUserResult(userFriend, userFriend.getItemUserList().size());
        }).collect(Collectors.toList());
    }
}
