package com.example.demo3springaimessenger.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // 4XX Errors
    METHOD_ARGUMENT_NOT_VALID(HttpStatus.BAD_REQUEST,"REQ-001", "잘못된 [인자]입니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "REQ-002", "요청한 [RESOURCE, URL]를 찾을 수 없습니다."),

    // 5XX Errors
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SYS-001", "[Server] 내부 에러가 발생했습니다."),;

    private final HttpStatus status;
    private final String code;
    private final String message;

}
