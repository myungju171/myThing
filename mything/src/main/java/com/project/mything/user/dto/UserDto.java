package com.project.mything.user.dto;

import lombok.*;

import java.time.LocalDate;

public class UserDto {

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ResponseSimpleUser {
        private Long userId;
        private String name;
        ImageDto.SimpleImageDto avatar;
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ResponseDetailUser {
        private Long userId;
        private String name;
        private String phone;
        private LocalDate birthday;
        private String infoMessage;
        ImageDto.SimpleImageDto avatar;
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UserInfo {
        private Long userId;
        private String email;
        private String name;
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RequestEditProFile {
        private String name;
        private String infoMessage;
        private LocalDate birthday;
        private ImageDto.SimpleImageDto avatar;
    }
}
