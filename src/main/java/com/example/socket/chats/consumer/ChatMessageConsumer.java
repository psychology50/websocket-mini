package com.example.socket.chats.consumer;

import com.example.socket.chats.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatMessageConsumer {
//    private final SimpMessagingTemplate simpMessagingTemplate; // RabbitMQ Messaging Template를 사용하면?
//    private final RabbitMessagingTemplate rabbitMessagingTemplate;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "${rabbitmq.chat-queue.name}")
//    @SendTo("/topic/chat.room.{roomId}")
    public void receiveMessage(ChatMessage message) {
        log.info("Consume Message = {}", message);
        String destination = "/topic/chat.room." + message.roomId();
        log.info("destination = {}", destination);
        rabbitTemplate.convertAndSend(destination, message);
    }
}
