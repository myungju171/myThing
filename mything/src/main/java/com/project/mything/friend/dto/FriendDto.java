package com.project.mything.friend.dto;

import com.project.mything.user.dto.UserDto;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

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
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ResponseSimpleFriend {
        private Long userId;
        private String name;
        private String infoMessage;
        private LocalDate birthDay;
        private Integer itemCount;
        private Long avatarId;
        private String remotePath;
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ResponseMultiFriend<T> {
        private List<T> data;
        private UserDto.ResponseSimpleUser userInfo;

        public ResponseMultiFriend(List<T> data, UserDto.ResponseSimpleUser responseSimpleUser) {
            this.data = data;
            userInfo = responseSimpleUser;
        }
    }
}
