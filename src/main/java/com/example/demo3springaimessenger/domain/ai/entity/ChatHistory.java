package com.example.demo3springaimessenger.domain.ai.entity;

import com.example.demo3springaimessenger.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chat_history")
public class ChatHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String conversationId;

    @Column(nullable = false)
    private String messageType;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Builder
    public ChatHistory(String conversationId, String messageType, String content) {
        this.conversationId = conversationId;
        this.messageType = messageType;
        this.content = content;
    }
}
