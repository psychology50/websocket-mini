package com.example.socket.chats.service;

import com.example.socket.chats.common.guid.IdGenerator;
import com.example.socket.chats.dto.ChatMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ChatMessageService {
    private final RabbitTemplate rabbitTemplate;
    private final IdGenerator<Long> idGenerator;
    private final String CHAT_EXCHANGE_NAME;

    public ChatMessageService(
            IdGenerator<Long> tsidGenerator,
            RabbitTemplate rabbitTemplate,
            @Value("${rabbitmq.chat-exchange.name}") String CHAT_EXCHANGE_NAME
    ) {
        this.idGenerator = tsidGenerator;
        this.rabbitTemplate = rabbitTemplate;
        this.CHAT_EXCHANGE_NAME = CHAT_EXCHANGE_NAME;
    }

    public void sendMessage(ChatMessage message) {
        // 아이디 생성

        // 키-쌍 저장소에 저장

        // 전송
        rabbitTemplate.convertAndSend(CHAT_EXCHANGE_NAME, "chat.room." + message.roomId(), message);
    }
}
