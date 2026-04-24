package com.example.demo3springaimessenger.domain.ai.service;

import com.example.demo3springaimessenger.domain.ai.entity.ChatHistory;
import com.example.demo3springaimessenger.domain.ai.repository.ChatHistoryRepository;
import com.example.demo3springaimessenger.global.encryptor.Encryptor;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DbChatMemory implements ChatMemory {

    private final ChatHistoryRepository chatHistoryRepository;
    private final Encryptor encryptor;

    @Override
    @Transactional
    public void add(String conversationId, List<Message> messages) {
        for (Message message : messages) {
            String encryptedContent = encryptor.encrypt(message.getText());
            ChatHistory history = ChatHistory.builder()
                    .conversationId(conversationId)
                    .messageType(message.getMessageType().name())
                    .content(encryptedContent)
                    .build();
            chatHistoryRepository.save(history);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> get(String conversationId) {
        List<ChatHistory> histories = chatHistoryRepository.findAllByConversationId(conversationId);
        
        return histories.stream()
                .map(this::mapToMessage)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void clear(String conversationId) {
        chatHistoryRepository.deleteAllByConversationId(conversationId);
    }

    private Message mapToMessage(ChatHistory history) {
        String decryptedContent = encryptor.decrypt(history.getContent());
        MessageType type = MessageType.valueOf(history.getMessageType());
        
        return switch (type) {
            case USER -> new UserMessage(decryptedContent);
            case ASSISTANT -> new AssistantMessage(decryptedContent);
            case SYSTEM -> new SystemMessage(decryptedContent);
            default -> throw new IllegalArgumentException("Unknown message type: " + type);
        };
    }
}
