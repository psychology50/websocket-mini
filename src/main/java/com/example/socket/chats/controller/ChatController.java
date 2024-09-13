package com.example.socket.chats.controller;

import com.example.socket.chats.dto.ChatMessage;
import com.example.socket.chats.producer.ChatMessageProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatMessageProducer chatMessageProducer;

    @MessageMapping("chat.message.{roomId}")
    public void sendMessage(@DestinationVariable String roomId, ChatMessage message) {
        log.info("sendMessage: roomId={}, message={}", roomId, message);
        chatMessageProducer.sendMessage(message);
    }
}