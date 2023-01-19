package com.project.mything.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;


@Getter
@NoArgsConstructor
public class ResponseException {
    private LocalDateTime timeTable;
    private String code;
    private String message;

    private Map<String, String> errorData;

    public ResponseException(String code, String message, Map<String, String> errorData) {
        this.timeTable = LocalDateTime.now();
        this.code = code;
        this.message = message;
        this.errorData = errorData;
    }

    public ResponseException(String code, String message) {
        timeTable = LocalDateTime.now();
        this.code = code;
        this.message = message;
    }

    public ResponseException(String code, Map<String, String> errorData) {
        this.timeTable = LocalDateTime.now();
        this.code = code;
        this.errorData = errorData;
    }

    public static ResponseEntity<ResponseException> toResponseEntity(ErrorCode errorCode) {
        ResponseException responseException =
                new ResponseException(errorCode.getHttpStatus().toString(), errorCode.toString());
        return new ResponseEntity<ResponseException>(responseException, errorCode.getHttpStatus());
    }

    public static ResponseEntity<ResponseException> toResponseEntity(
            MethodArgumentNotValidException methodArgumentNotValidException) {
        Map<String, String> errorData = new HashMap<>();

        methodArgumentNotValidException.getAllErrors()
                .forEach(error ->
                        errorData.put(((FieldError) error).getField(), error.getDefaultMessage()
                        ));

        ResponseException responseException = new ResponseException(BAD_REQUEST.toString(), errorData);

        return new ResponseEntity<ResponseException>(responseException, BAD_REQUEST);
    }

    public static ResponseEntity<ResponseException> toResponseEntity(
            MissingServletRequestParameterException missingServletRequestParameterException
    ) {
        Map<String, String> errorData = new HashMap<>();

        String parameterName = missingServletRequestParameterException.getParameterName();
        String message = missingServletRequestParameterException.getMessage();
        errorData.put("parameterName", parameterName);
        errorData.put("message", message);

        ResponseException responseException = new ResponseException(BAD_REQUEST.toString(), errorData);

        return new ResponseEntity<ResponseException>(responseException, BAD_REQUEST);
    }

    public static ResponseEntity<ResponseException> toResponseEntity(
            HttpRequestMethodNotSupportedException httpRequestMethodNotSupportedException
    ) {
        Map<String, String> errorData = new HashMap<>();
        String message = httpRequestMethodNotSupportedException.getMessage();

        httpRequestMethodNotSupportedException.getSupportedHttpMethods().forEach(error ->
            errorData.put(error.name(), "사용가능"));


        ResponseException responseException = new ResponseException(BAD_REQUEST.toString(), message ,errorData);

        return new ResponseEntity<ResponseException>(responseException, BAD_REQUEST);
    }
}
