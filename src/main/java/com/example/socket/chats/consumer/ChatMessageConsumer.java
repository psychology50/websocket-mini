package com.example.socket.chats.consumer;

import com.example.socket.chats.dto.ChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageConsumer {
    private final RabbitMessagingTemplate rabbitMessagingTemplate;

    @RabbitListener(queues = "${rabbitmq.chat-queue.name}")
    public void receiveMessage(ChatMessage message) {
        log.info("Consume Message = {}", message);
        String destination = "/topic/chat.room." + message.roomId();
        log.info("destination = {}", destination);

        rabbitMessagingTemplate.convertAndSend(destination, message);
    }
}
