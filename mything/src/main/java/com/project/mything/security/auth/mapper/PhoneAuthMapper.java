package com.project.mything.security.auth.mapper;

import com.project.mything.security.auth.dto.PhoneAuthDto;
import com.project.mything.security.auth.entity.PhoneAuth;
import com.project.mything.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", typeConversionPolicy = ReportingPolicy.IGNORE)
public interface PhoneAuthMapper {

    PhoneAuth toPhoneAuth(PhoneAuthDto.RequestAuthNumber requestAuthNumber, String authNumber);

    User toUser(PhoneAuthDto.RequestJoin join);


}
