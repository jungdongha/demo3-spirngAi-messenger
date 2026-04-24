package com.example.demo3springaimessenger.domain.ai.config;

import com.example.demo3springaimessenger.domain.ai.repository.ChatHistoryRepository;
import com.example.demo3springaimessenger.domain.ai.service.DbChatMemory;
import com.example.demo3springaimessenger.global.encryptor.Encryptor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    public ChatMemory chatMemory(ChatHistoryRepository repository, Encryptor encryptor) {
        return new DbChatMemory(repository, encryptor);
    }

    @Bean(name = "systemPromptTemplate")
    public String systemPromptTemplate() {
        return """
                You are a helpful AI assistant.
                Current Date: {currentDate}
                """;
    }
}
