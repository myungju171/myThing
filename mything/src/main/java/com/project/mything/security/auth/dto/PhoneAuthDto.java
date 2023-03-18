package com.project.mything.security.auth.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

public class PhoneAuthDto {
    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RequestJoin {
        @NotBlank
        private  String email;
        @NotBlank
        private String name;
        @NotBlank
        private String password;
        @NotNull
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate birthDay;
        @NotBlank
        @Size(min = 11, max = 11)
        private String phone;

        private String authNumber;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RequestAuthNumber {
        @NotBlank
        @Size(min = 11, max = 11)
        private String phone;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RequestLogin {
        private String email;
        private String password;
    }
}
