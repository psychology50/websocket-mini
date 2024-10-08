package com.example.socket.domains.chat.domain;

import java.io.Serializable;
import java.util.Objects;

public class ChatId implements Serializable {
    private Long id;
    private Long channelId;

    protected ChatId(Long id, Long channelId) {
        this.id = Objects.requireNonNull(id);
        this.channelId = Objects.requireNonNull(channelId);
    }

    public static ChatId of(Long id, Long channelId) {
        return new ChatId(id, channelId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChatId that)) return false;
        return id.equals(that.id) && channelId.equals(that.channelId);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = ((1 << 5) - 1) * result + channelId.hashCode();
        return result;
    }
}
