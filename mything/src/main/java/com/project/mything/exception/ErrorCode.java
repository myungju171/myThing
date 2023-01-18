package com.project.mything.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    NO_MATCH_AUTH_NUMBER(BAD_REQUEST),
    NO_MATCH_PHONE_NUMBER(BAD_REQUEST),
    ;

    private HttpStatus httpStatus;


}
