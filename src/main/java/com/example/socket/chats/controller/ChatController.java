package com.example.socket.chats.controller;

import com.example.socket.chats.common.annotation.PreAuthorize;
import com.example.socket.chats.dto.ChatMessage;
import com.example.socket.chats.service.ChatMessageService;
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
    private final ChatMessageService chatMessageService;

    @MessageMapping("chat.message.{roomId}")
    @PreAuthorize("#isAuthenticated(#principal) and @chatRoomAccessChecker.hasPermission(#roomId, 1L)") // forbidden처리 된 access token으로 접근하면?
    public void sendMessage(@DestinationVariable String roomId, ChatMessage message, Principal principal) {
        log.info("sendMessage {}: ", message);
        chatMessageService.sendMessage(message);
    }

    @MessageMapping("chat.message.exception")
    public void exceptionMessage(ChatMessage message, Principal principal) {
        throw new RuntimeException("강제로 발생한 예외");
    }
}
