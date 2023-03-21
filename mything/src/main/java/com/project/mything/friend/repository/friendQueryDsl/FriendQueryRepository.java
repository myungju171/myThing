package com.project.mything.friend.repository.friendQueryDsl;

import com.project.mything.friend.dto.FriendDto;
import com.project.mything.friend.entity.enums.FriendStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface FriendQueryRepository {
    public abstract Page<FriendDto.ResponseSimpleFriend> getFriendList(Long userId, FriendStatus friendStatus, Boolean isBirthday, Pageable pageable);
}
