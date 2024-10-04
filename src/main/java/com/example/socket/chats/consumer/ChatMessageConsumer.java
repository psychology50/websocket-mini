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
        // 키-쌍 저장소에 저장

        // 메시지의 roomId에 속한 모든 사용자 정보 조회

        // 각 사용자의 상태에 따라 메시지 전달
    }
}
