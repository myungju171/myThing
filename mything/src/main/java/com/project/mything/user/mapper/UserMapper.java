package com.project.mything.user.mapper;

import com.project.mything.user.dto.UserDto;
import com.project.mything.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", typeConversionPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "user.id", target = "userId")
    UserDto.ResponseUserId toResponseUserId(User user);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.avatar.remotePath", target = "image")
    UserDto.ResponseSimpleUser toResponseSimpleUser(User user);

    @Mapping(source = "dbUser.id", target = "userId")
    @Mapping(source = "dbUser.avatar.id", target = "avatarId")
    @Mapping(source = "dbUser.avatar.remotePath", target = "remotePath")
    UserDto.ResponseImageURl toResponseImageUrl(User dbUser);
}
