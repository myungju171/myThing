package com.project.mything.friend.mapper;

import com.project.mything.friend.dto.FriendDto;
import com.project.mything.friend.entity.Friend;
import com.project.mything.user.dto.UserDto;
import com.project.mything.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", typeConversionPolicy = ReportingPolicy.IGNORE)
public interface FriendMapper {
    FriendDto.ResponseSimpleFriend toResponseSimpleFriend(UserDto.ResponseDetailUser user, Integer itemCount);

    @Mapping(target = "id", ignore = true)
    Friend toFriend(User user, User userFriend);
}
