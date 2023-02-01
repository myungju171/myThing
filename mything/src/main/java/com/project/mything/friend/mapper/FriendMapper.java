package com.project.mything.friend.mapper;

import com.project.mything.friend.dto.FriendDto;
import com.project.mything.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", typeConversionPolicy = ReportingPolicy.IGNORE)
public interface FriendMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.name", target = "name")
    @Mapping(source = "user.infoMessage", target = "infoMessage")
    @Mapping(source = "user.birthDay", target = "birthDay")
    @Mapping(source = "user.avatar.id", target = "avatarId")
    @Mapping(source = "user.avatar.remotePath", target = "remotePath")
    FriendDto.ResponseSimpleFriend toResponseFindUserResult(User user, Integer itemCount);

}
