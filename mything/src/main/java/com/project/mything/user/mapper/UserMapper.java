package com.project.mything.user.mapper;

import com.project.mything.user.dto.UserDto;
import com.project.mything.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", typeConversionPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.image.id", target = "avatar.imageId")
    @Mapping(source = "user.image.remotePath", target = "avatar.remotePath")
    UserDto.ResponseSimpleUser toResponseSimpleUser(User user);

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.image.id", target = "avatar.imageId")
    @Mapping(source = "user.image.remotePath", target = "avatar.remotePath")
    UserDto.ResponseDetailUser toResponseDetailUser(User user);

    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.password", target = "password")
    UserDto.SecurityUserDetail toSecurityUserDetail(User user);
}
