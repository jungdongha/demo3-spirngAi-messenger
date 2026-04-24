package com.example.demo3springaimessenger.domain.ai.dto.request;

public record AiChatRequest(
        String message,
        String conversationId,
        String model

) {
}
