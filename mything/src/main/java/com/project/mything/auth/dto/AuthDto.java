package com.project.mything.auth.dto;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.*;
import java.time.LocalDate;

public class AuthDto {
    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RequestJoin {
        @NotBlank
        @Email
        private String email;
        @NotBlank
        // 영문, 숫자, 한글 2자 이상 16자 이하(공백 및 초성, 자음 불가능)
        @Pattern(regexp = "^(?=.*[a-z0-9가-힣])[a-z0-9가-힣].{0,6}$", message = "한글 2자 이상 16자 이하입니다.")
        private String name;
        @NotBlank
        // 영문 + 숫자 + 특수문자 8자 이상 20자 이하
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$", message = "영문+숫자+특수문자 8자 이상 20자 이하 입니다.")
        private String password;
        @NotNull
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate birthday;
        @NotBlank
        @Size(min = 11, max = 11, message = "'-'를 제외하고 작성해주세요.")
        private String phone;
        @NotBlank
        @Size(min = 4, max = 4)
        private String authNumber;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RequestAuthNumber {
        @NotBlank
        @Size(min = 11, max = 11, message = "'-'를 제외하고 작성해주세요.")
        private String phone;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RequestLogin {
        @NotBlank
        @Email
        private String email;
        @NotBlank
        // 영문 + 숫자 + 특수문자 8자 이상 20자 이하
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[~!@#$%^&*()+|=])[A-Za-z\\d~!@#$%^&*()+|=]{8,16}$", message = "영문+숫자+특수문자 8자 이상 20자 이하 입니다.")
        private String password;
    }

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ResponseLogin {
        private Long userId;
        private String accessToken;
    }
}
