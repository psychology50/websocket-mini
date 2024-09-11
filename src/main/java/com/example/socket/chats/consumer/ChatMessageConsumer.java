package com.example.socket.chats.consumer;

import com.example.socket.chats.dto.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ChatMessageConsumer {
    private final SimpMessagingTemplate simpMessagingTemplate;

    public ChatMessageConsumer(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @RabbitListener(queues = "${rabbitmq.chat-queue.name}")
    public void receiveMessage(ChatMessage message) {
        log.info("message = {}", message);
        simpMessagingTemplate.convertAndSend("/sub/chat.room.", message.roomId());
    }
}
