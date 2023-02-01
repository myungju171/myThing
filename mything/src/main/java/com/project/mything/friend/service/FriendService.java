package com.project.mything.friend.service;

import com.project.mything.friend.dto.FriendDto;
import com.project.mything.friend.mapper.FriendMapper;
import com.project.mything.friend.repository.FriendRepository;
import com.project.mything.user.entity.User;
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
    public FriendDto.ResponseSimpleFriend searchFriend(String friendPhone) {
        User dbFriend
                = userService.findUserWithItemUserByPhone(friendPhone);

        Integer ItemCountOfDbFriend = dbFriend.getItemUserList().size();
        return friendMapper.toResponseFindUserResult(dbFriend, ItemCountOfDbFriend);
    }

    public List<FriendDto.ResponseSimpleFriend> getFriends(Long userId) {
        User dbUser = userService.findVerifiedUser(userId);

        return  dbUser.getFriendList().stream().map(friend -> {
            User userFriend = userService.findVerifiedUser(friend.getUserFriendId());
            return friendMapper.toResponseFindUserResult(userFriend, userFriend.getItemUserList().size());
        }).collect(Collectors.toList());
    }
}
