package com.example.socket.chats.common.handler;

import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

public interface StompCommandHandler {
    boolean supports(StompCommand command);
    void handle(Message<?> message, StompHeaderAccessor accessor);
}
