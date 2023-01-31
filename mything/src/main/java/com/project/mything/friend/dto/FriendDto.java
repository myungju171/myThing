package com.project.mything.friend.dto;

import lombok.*;

import java.time.LocalDate;

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
    public static class ResponseFindUserResult {
        private Long userId;
        private String name;
        private String infoMessage;
        private LocalDate birthDay;
        private Integer itemCount;
    }

}
