package com.example.socket.chats.controller;

import com.example.socket.chats.common.annotation.PreAuthorize;
import com.example.socket.chats.dto.ChatMessage;
import com.example.socket.chats.producer.ChatMessageProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatMessageProducer chatMessageProducer;

    @MessageMapping("chat.message.{roomId}")
    @PreAuthorize("isAnonymous()")
    public void sendMessage(@DestinationVariable String roomId, ChatMessage message, Principal principal) {
        chatMessageProducer.sendMessage(message);
    }

    @MessageMapping("chat.message.exception")
    public void exceptionMessage(ChatMessage message, Principal principal) {
        throw new RuntimeException("강제로 발생한 예외");
    }
}
