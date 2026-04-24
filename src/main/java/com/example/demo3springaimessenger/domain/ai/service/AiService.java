package com.example.demo3springaimessenger.domain.ai.service;

import com.example.demo3springaimessenger.domain.ai.exception.AiException;
import com.example.demo3springaimessenger.domain.ai.exception.AiExceptionInformation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AiService {
    private final Map<String, ChatModel> chatModels;
    private final ChatMemory chatMemory;
    private final String systemPromptTemplate;

    public AiService(
            Map<String, ChatModel> chatModels,
            ChatMemory chatMemory,
            @Qualifier("systemPromptTemplate") String systemPromptTemplate
    ) {
        this.chatModels = chatModels;
        this.chatMemory = chatMemory;
        this.systemPromptTemplate = systemPromptTemplate;
    }

    public String chat(String modelName, String conversationId, String message) {
        String modelBeanName;
        
        if (modelName == null || modelName.isBlank()) {
            modelBeanName = "openAiChatModel";
        } else if (modelName.equalsIgnoreCase("claude")) {
            modelBeanName = "anthropicChatModel";
        } else if (modelName.equalsIgnoreCase("gemini")) {
            modelBeanName = "googleGenAiChatModel";
        } else {
            modelBeanName = modelName + "ChatModel";
        }

        if (!chatModels.containsKey(modelBeanName)) {
            log.error("Model not found: {}. Available: {}", modelBeanName, chatModels.keySet());
            throw new AiException(AiExceptionInformation.MODEL_NOT_FOUND);
        }
        
        ChatModel selectedModel = chatModels.get(modelBeanName);

        String currentDate = LocalDate.now().toString();
        String systemPrompt = systemPromptTemplate.replace("{currentDate}", currentDate);

        // 1. Get history
        List<Message> history = chatMemory.get(conversationId);
        
        // 2. Prepare all messages
        List<Message> allMessages = new ArrayList<>();
        allMessages.add(new SystemMessage(systemPrompt));
        allMessages.addAll(history);
        
        UserMessage userMessage = new UserMessage(message);
        allMessages.add(userMessage);

        try {
            // 3. Call model
            ChatResponse response = selectedModel.call(new Prompt(allMessages));
            String assistantContent = response.getResult().getOutput().getText();

            // 4. Update memory with NEW messages (User + Assistant)
            List<Message> newMessages = List.of(userMessage, new AssistantMessage(assistantContent));
            chatMemory.add(conversationId, newMessages);

            return assistantContent;
        } catch (Exception e) {
            log.error("AI Model Error [model: {}, conversationId: {}]: {}", modelBeanName, conversationId, e.getMessage(), e);
            throw new AiException(AiExceptionInformation.AI_CLIENT_ERROR);
        }
    }
}
