package com.project.mything.friend.service;

import com.project.mything.friend.dto.FriendDto;
import com.project.mything.friend.mapper.FriendMapper;
import com.project.mything.friend.repository.FriendRepository;
import com.project.mything.user.entity.User;
import com.project.mything.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
    private final FriendMapper friendMapper;
    private final UserService userService;
    public FriendDto.ResponseFindUserResult searchFriend(String friendPhone) {
        User dbFriend
                = userService.findUserWithItemUserByPhone(friendPhone);

        Integer ItemCountOfDbFriend = dbFriend.getItemUserList().size();
        return friendMapper.toResponseFindUserResult(dbFriend, ItemCountOfDbFriend);
    }

}
