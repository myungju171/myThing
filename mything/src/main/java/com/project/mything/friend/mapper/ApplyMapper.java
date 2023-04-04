package com.project.mything.friend.mapper;

import com.project.mything.friend.dto.ApplyDto;
import com.project.mything.friend.entity.Apply;
import com.project.mything.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", typeConversionPolicy = ReportingPolicy.IGNORE)
public interface ApplyMapper {

    @Mapping(target = "id", ignore = true)
    Apply ToApply(User sendUser, User receiveUser);


    ApplyDto.ResponseApplyId toResponseApplyId(Long applyId);
}
