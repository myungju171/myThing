package com.project.mything.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    NO_MATCH_AUTH_NUMBER(NOT_FOUND),
    NO_MATCH_PHONE_NUMBER(NOT_FOUND),
    INVALID_PHONE_NUMBER(BAD_REQUEST),
    USER_NOT_FOUND(NOT_FOUND),
    ITEM_EXISTS(CONFLICT),
    INCORRECT_QUERY_REQUEST(BAD_REQUEST),
    ITEM_NOT_FOUND(NOT_FOUND),;

    private HttpStatus httpStatus;


}
