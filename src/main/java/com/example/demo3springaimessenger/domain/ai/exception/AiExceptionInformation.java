package com.example.demo3springaimessenger.domain.ai.exception;

import com.example.demo3springaimessenger.global.exception.ExceptionInformation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AiExceptionInformation implements ExceptionInformation {

    MODEL_NOT_FOUND(HttpStatus.BAD_REQUEST, "AI-001", "지원하지 않는 AI 모델입니다."),
    AI_CLIENT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AI-002", "AI 서비스 호출 중 오류가 발생했습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
