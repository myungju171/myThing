package com.project.mything.auth.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

public class PhoneAuthDto {
    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RequestJoin {
        @NotBlank
        private String name;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate birthDay;
        @NotBlank
        private String phone;
        @NotBlank
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
