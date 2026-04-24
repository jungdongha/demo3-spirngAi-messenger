package com.example.demo3springaimessenger.domain.ai.repository;

import com.example.demo3springaimessenger.domain.ai.entity.ChatHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {
    List<ChatHistory> findAllByConversationId(String conversationId);
    void deleteAllByConversationId(String conversationId);
}
