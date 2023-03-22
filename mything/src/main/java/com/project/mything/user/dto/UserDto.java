package com.project.mything.user.dto;

import com.querydsl.core.annotations.QueryProjection;
import com.project.mything.image.dto.ImageDto;
import lombok.*;
import reactor.util.annotation.Nullable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public class UserDto {

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ResponseSimpleUser {
        private Long userId;
        private String name;
        ImageDto.SimpleImageDto avatar;

        @QueryProjection
        public ResponseSimpleUser(Long userId, String name, ImageDto.SimpleImageDto avatar) {
            this.userId = userId;
            this.name = name;
            this.avatar = avatar;
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ResponseDetailUser {
        private Long userId;
        private String name;
        private String phone;
        private LocalDate birthday;
        private String infoMessage;
        ImageDto.SimpleImageDto avatar;

        @QueryProjection
        public ResponseDetailUser(Long userId, String name, String phone, LocalDate birthday, String infoMessage, ImageDto.SimpleImageDto avatar) {
            this.userId = userId;
            this.name = name;
            this.phone = phone;
            this.birthday = birthday;
            this.infoMessage = infoMessage;
            this.avatar = avatar;
        }
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
        @NotBlank
        private String name;
        @NotNull
        private String infoMessage;
        @NotNull
        private LocalDate birthday;
        @Nullable
        private ImageDto.SimpleImageDto avatar;
    }
}
