package com.project.mything.user.dto;

import lombok.*;

public class UserDto {

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ResponseUserId {
        private Long userId;
    }


}
