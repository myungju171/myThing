package com.project.mything.auth.dto;

import lombok.*;

public class PhoneAuthDto {
    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RequestJoin {
        private String name;
        private String birthDay;
        private String phone;
        private String authNumber;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RequestAuthNumber {
        private String phone;
    }


}
