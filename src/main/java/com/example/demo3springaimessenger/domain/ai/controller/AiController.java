package com.example.demo3springaimessenger.domain.ai.controller;

import com.example.demo3springaimessenger.domain.ai.dto.request.AiChatRequest;
import com.example.demo3springaimessenger.domain.ai.dto.response.AiChatResponse;
import com.example.demo3springaimessenger.domain.ai.service.AiService;
import com.example.demo3springaimessenger.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "AI", description = "AI 채팅 API")
public class AiController {
    private final AiService aiService;

    @PostMapping("/chat")
    @Operation(summary = "AI 채팅", description = "AI에게 메시지를 보내고 응답을 받습니다 (model 파라미터로 claude, groq, gemini 선택 가능)")
    public ApiResponse<AiChatResponse> chat(@RequestBody AiChatRequest request) {
        String conversationId = request.conversationId() != null
                ? request.conversationId()
                : UUID.randomUUID().toString();
        String content = aiService.chat(request.model(), conversationId, request.message());
        return ApiResponse.response(HttpStatus.OK, "채팅 성공", new AiChatResponse(content, conversationId));
    }
}
