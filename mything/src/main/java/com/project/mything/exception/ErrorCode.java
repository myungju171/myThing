package com.project.mything.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    NO_MATCH_AUTH_NUMBER(CONFLICT),
    NO_MATCH_PHONE_NUMBER(CONFLICT),
    USER_NOT_FOUND(NOT_FOUND),
    ITEM_EXISTS(CONFLICT),
    ITEM_NOT_FOUND(NOT_FOUND),
    USER_NOT_MATCH(FORBIDDEN),
    ITEM_ALREADY_RESERVED(CONFLICT),
    RESERVE_USER_CONFLICT(CONFLICT),
    S3_SERVICE_ERROR(INTERNAL_SERVER_ERROR),
    AVATAR_MUST_NOT_NULL(CONFLICT),
    APPLY_NOT_FOUND(NOT_FOUND),
    ITEM_STATUS_CONFLICT(CONFLICT),
    NO_CORRECT_ACCOUNT(CONFLICT),
    IMAGE_NOT_FOUND(NOT_FOUND),
    EMAIL_ALREADY_EXIST(CONFLICT),
    PHONE_ALREADY_EXIST(CONFLICT),
    APPLY_SENDER_CONFLICT(CONFLICT),
    APPLY_RECEIVER_CONFLICT(CONFLICT),
    MESSAGE_ALREADY_SEND(CONFLICT),
    JWT_EXPIRED_TOKEN(UNAUTHORIZED),
    JWT_SIGNATURE_DIFFERENT(UNAUTHORIZED),
    JWT_UNSUPPORTED_TOKEN(UNAUTHORIZED),
    JWT_INVALID_TOKEN(UNAUTHORIZED),
    JWT_MALFORMED_EXCEPTION(UNAUTHORIZED),
    JWT_INVALID_PAYLOAD(UNAUTHORIZED),
    FRIEND_NOT_FOUND(NOT_FOUND),
    SEND_APPLY_ALREADY_EXIST(FORBIDDEN),
    NAVER_JSON_ERROR(INTERNAL_SERVER_ERROR),
    RECEIVED_APPLY_APPLY_EXIST(CONFLICT);

    private HttpStatus httpStatus;


}
