package com.project.mything.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler({BusinessLogicException.class})
    public ResponseEntity<ResponseException> businessLogicException(BusinessLogicException businessLogicException) {
        return ResponseException.toResponseEntity(businessLogicException.getErrorCode());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ResponseException> methodArgumentNotValidException(MethodArgumentNotValidException methodArgumentNotValidException) {
        return ResponseException.toResponseEntity(methodArgumentNotValidException);
    }
}
