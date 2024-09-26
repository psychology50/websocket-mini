package com.example.socket.chats.dto;

import java.util.Objects;

public record ErrorMessage(String reason) {
    public ErrorMessage {
        Objects.requireNonNull(reason, "reason must not be null");
    }
}
