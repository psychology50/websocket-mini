package com.example.socket.domains.chat;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.Objects;

@RedisHash("chat")
public class ChatCache {
    @Id
    private ChatId id;
    private Long messageFrom;
    private String content;
    private LocalDateTime createdAt;

    @Builder
    private ChatCache(ChatId id, Long messageFrom, String content, LocalDateTime createdAt) {
        if (content.length() > 5000) {
            throw new IllegalArgumentException("메시지는 5,000자 이하로 입력해주세요.");
        }

        this.id = Objects.requireNonNull(id);
        this.messageFrom = Objects.requireNonNull(messageFrom);
        this.content = Objects.requireNonNull(content);
        this.createdAt = Objects.requireNonNull(createdAt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatCache that)) return false;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
