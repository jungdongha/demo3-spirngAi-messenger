package com.example.demo3springaimessenger.global.exception;

import org.springframework.http.HttpStatus;

public interface ExceptionInformation {
    HttpStatus getHttpStatus();
    String getCode();
    String getMessage();
}
