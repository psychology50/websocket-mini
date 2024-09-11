package com.example.socket.chats.dto;

public record ChatMessage(
        String roomId,
        String content
) {
}
