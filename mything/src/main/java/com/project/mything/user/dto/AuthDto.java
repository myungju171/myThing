package com.project.mything.user.dto;

import lombok.*;

public class AuthDto {

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Login{
        private String email;
        private String password;
    }

}
