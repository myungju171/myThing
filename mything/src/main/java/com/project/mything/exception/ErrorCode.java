package com.project.mything.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    NO_MATCH_AUTH_NUMBER(BAD_REQUEST,"정확한 인증번호를 입력해주세요 "),

    ;

    private HttpStatus httpStatus;
    private String message;

}
