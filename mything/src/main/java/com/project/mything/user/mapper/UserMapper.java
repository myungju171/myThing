package com.project.mything.user.mapper;

import com.project.mything.auth.dto.PhoneAuthDto;
import com.project.mything.user.dto.UserDto;
import com.project.mything.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", typeConversionPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    @Mapping(source = "user.id", target = "userId")
    UserDto.ResponseUserId toResponseUserId(User user);
}
