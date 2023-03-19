package com.project.mything.security.jwt.service;

import com.project.mything.user.dto.UserDto;

public interface JwtParseToken {
    public UserDto.UserInfo getUserInfo(String token);

}
