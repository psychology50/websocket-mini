package com.example.socket.chats.service;

import com.example.socket.chats.dto.ChatMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ChatMessageService {
    private final RabbitTemplate rabbitTemplate;
    private final String CHAT_EXCHANGE_NAME;

    public ChatMessageService(RabbitTemplate rabbitTemplate,
                              @Value("${rabbitmq.chat-exchange.name}") String CHAT_EXCHANGE_NAME) {
        this.rabbitTemplate = rabbitTemplate;
        this.CHAT_EXCHANGE_NAME = CHAT_EXCHANGE_NAME;
    }

    public void sendMessage(ChatMessage message) {
        rabbitTemplate.convertAndSend(CHAT_EXCHANGE_NAME, "chat.room." + message.roomId(), message);
    }
}
