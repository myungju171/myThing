package com.project.mything.friend.dto;

import com.project.mything.user.dto.UserDto;
import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

public class FriendDto {

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RequestFindUser {
        private String phone;
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ResponseSimpleFriend {
        UserDto.ResponseDetailUser user;
        private Integer itemCount;

        @QueryProjection
        public ResponseSimpleFriend(UserDto.ResponseDetailUser user, Integer itemCount) {
            this.user = user;
            this.itemCount = itemCount;
        }
    }

}
