package com.project.mything.auth.mapper;

import com.project.mything.auth.dto.AuthDto;
import com.project.mything.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", typeConversionPolicy = ReportingPolicy.IGNORE)
public interface AuthMapper {

    User toUser(AuthDto.RequestJoin join);

    @Mapping(source = "dbUser.id", target = "userId")
    @Mapping(source = "token", target = "accessToken")
    AuthDto.ResponseLogin toResponseToken(User dbUser, String token);
}
