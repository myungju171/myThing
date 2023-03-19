package com.project.mything.user.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

public class UserDto {

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ResponseSimpleUser {
        private Long userId;
        private String name;
        @Builder.Default
        private String image = "";
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ResponseImageURl {
        private Long userId;
        private Long avatarId;
        @Builder.Default
        private String remotePath = "";
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RequestUserId {
        @NotNull
        @Positive
        private Long userId;
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ResponseDetailUser {
        private Long userId;
        private String name;
        private String phone;
        private LocalDate birthDay;
        private String infoMessage;
        private Long avatarId;
        private String image;
    }
    
}
