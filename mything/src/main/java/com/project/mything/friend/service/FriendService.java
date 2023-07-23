package com.project.mything.friend.service;

import com.project.mything.exception.BusinessLogicException;
import com.project.mything.exception.ErrorCode;
import com.project.mything.friend.dto.FriendDto;
import com.project.mything.friend.entity.Friend;
import com.project.mything.friend.entity.enums.FriendStatus;
import com.project.mything.friend.mapper.FriendMapper;
import com.project.mything.friend.repository.FriendRepository;
import com.project.mything.page.ResponseMultiPageDto;
import com.project.mything.redis.config.RedisCacheKeys;
import com.project.mything.user.dto.UserDto;
import com.project.mything.user.entity.User;
import com.project.mything.user.mapper.UserMapper;
import com.project.mything.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class FriendService {

    private final FriendRepository friendRepository;
    private final FriendMapper friendMapper;
    private final UserService userService;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public FriendDto.ResponseSimpleFriend searchFriend(String friendPhone) {
        User dbFriend = userService.findUserWithItemUserByPhone(friendPhone);
        return friendMapper.toResponseSimpleFriend(userMapper.toResponseDetailUser(dbFriend), dbFriend.getItemUserList().size());
    }

    @Transactional(readOnly = true)
    @Cacheable(key = "#isBirthday.booleanValue()", value = RedisCacheKeys.FRIEND_LIST,cacheManager = "redisCacheManager")
    public ResponseMultiPageDto<FriendDto.ResponseSimpleFriend> getFriendsList(UserDto.UserInfo userInfo,
                                                                               FriendStatus friendStatus,
                                                                               Boolean isBirthday) {
        Page<FriendDto.ResponseSimpleFriend> friendList =
                friendRepository.getFriendList(userInfo.getUserId(),
                        friendStatus,
                        isBirthday,
                        PageRequest.of(0, 999));
        return new ResponseMultiPageDto<>(friendList.getContent(), friendList);
    }

    public void createFriend(Long sendUserId, Long receiveUserId) {
        User sendUser = userService.findVerifiedUser(sendUserId);
        User receiveUser = userService.findVerifiedUser(receiveUserId);
        saveFriend(sendUser, receiveUser);
        saveFriend(receiveUser, sendUser);
    }

    private void saveFriend(User sendUser, User receiveUser) {
        Friend senderFriend = friendMapper.toFriend(sendUser, receiveUser);
        sendUser.getFriendList().add(senderFriend);
        friendRepository.save(senderFriend);
    }

    public void deleteFriend(UserDto.UserInfo userInfo, Long deleteUserId) {
        Friend requestUserFriend = findFriend(userInfo.getUserId(), deleteUserId);
        Friend receiveUserFriend = findFriend(deleteUserId, userInfo.getUserId());
        deleteFriendEntity(receiveUserFriend);
        deleteFriendEntity(requestUserFriend);
    }

    private Friend findFriend(Long userId, Long userFriendId) {
        return friendRepository.findFriend(userId, userFriendId)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.FRIEND_NOT_FOUND));
    }

    private void deleteFriendEntity(Friend dbFriend) {
        dbFriend.getUser().getFriendList().remove(dbFriend);
        friendRepository.delete(dbFriend);
    }


}
