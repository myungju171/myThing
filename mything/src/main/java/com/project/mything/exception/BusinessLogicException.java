package com.project.mything.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class BusinessLogicException extends RuntimeException{

    private final ErrorCode errorCode;

    public BusinessLogicException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

}
