package com.project.mything.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.aspectj.weaver.ast.Not;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    NO_MATCH_AUTH_NUMBER(NOT_FOUND),
    NO_MATCH_PHONE_NUMBER(NOT_FOUND),
    INVALID_PHONE_NUMBER(BAD_REQUEST),
    USER_NOT_FOUND(NOT_FOUND),;

    private HttpStatus httpStatus;


}
