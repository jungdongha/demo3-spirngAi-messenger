package com.example.demo3springaimessenger.domain.ai.exception;

import com.example.demo3springaimessenger.global.exception.BaseException;
import com.example.demo3springaimessenger.global.exception.ExceptionInformation;

public class AiException extends BaseException {
    public AiException(ExceptionInformation exceptionInformation) {
        super(exceptionInformation);
    }
}

