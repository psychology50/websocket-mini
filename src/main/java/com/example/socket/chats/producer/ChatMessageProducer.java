package com.example.socket.chats.producer;

import com.example.socket.chats.dto.ChatMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ChatMessageProducer {
    private final RabbitTemplate rabbitTemplate;
    private final String CHAT_EXCHANGE_NAME;
    private final String CHAT_ROUTING_KEY;

    public ChatMessageProducer(RabbitTemplate rabbitTemplate,
                               @Value("${rabbitmq.chat-exchange.name}") String CHAT_EXCHANGE_NAME,
                               @Value("${rabbitmq.chat-routing.key}") String CHAT_ROUTING_KEY) {
        this.rabbitTemplate = rabbitTemplate;
        this.CHAT_EXCHANGE_NAME = CHAT_EXCHANGE_NAME;
        this.CHAT_ROUTING_KEY = CHAT_ROUTING_KEY;
    }

    public void sendMessage(ChatMessage message) {
        rabbitTemplate.convertAndSend(CHAT_EXCHANGE_NAME, CHAT_ROUTING_KEY + message.roomId(), message);
    }
}
