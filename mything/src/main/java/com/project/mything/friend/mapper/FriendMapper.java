package com.project.mything.friend.mapper;

import com.project.mything.friend.dto.FriendDto;
import com.project.mything.friend.entity.Friend;
import com.project.mything.image.dto.ImageDto;
import com.project.mything.user.dto.UserDto;
import com.project.mything.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", typeConversionPolicy = ReportingPolicy.IGNORE)
public interface FriendMapper {

    default FriendDto.ResponseSimpleFriend toResponseSimpleFriend(User user, Integer itemCount) {
        UserDto.ResponseDetailUser responseDetailUser = UserDto.ResponseDetailUser.builder()
                .userId(user.getId())
                .name(user.getName())
                .phone(user.getPhone())
                .infoMessage(user.getInfoMessage())
                .birthday(user.getBirthday())
                .avatar(ImageDto.SimpleImageDto.builder()
                        .imageId(user.getImage().getId())
                        .remotePath(user.getImage().getRemotePath())
                        .build())
                .build();
        return FriendDto.ResponseSimpleFriend.builder()
                .user(responseDetailUser)
                .itemCount(itemCount)
                .build();
    }

    @Mapping(target = "id", ignore = true)
    Friend toFriend(User user, User userFriend);
}
