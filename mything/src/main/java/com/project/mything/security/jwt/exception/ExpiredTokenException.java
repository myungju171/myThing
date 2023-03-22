package com.project.mything.security.jwt.exception;

import io.jsonwebtoken.JwtException;
import lombok.Getter;

@Getter
public class ExpiredTokenException extends JwtException {

    public ExpiredTokenException(String message) {
        super(message);
    }

}
