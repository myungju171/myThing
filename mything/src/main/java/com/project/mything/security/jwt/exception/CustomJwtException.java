package com.project.mything.security.jwt.exception;

import com.project.mything.exception.ErrorCode;
import io.jsonwebtoken.JwtException;
import lombok.Getter;


public class CustomJwtException extends JwtException {

    @Getter
    private final ErrorCode errorCode;

    public CustomJwtException(ErrorCode errorCode) {
        super(null);
        this.errorCode = errorCode;
    }

}
