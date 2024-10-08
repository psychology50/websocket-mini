package com.example.socket.chats.service;

import com.example.socket.chats.common.guid.IdGenerator;
import com.example.socket.chats.common.security.principal.UserPrincipal;
import com.example.socket.chats.dto.ChatMessage;
import com.example.socket.domains.chat.domain.ChatCache;
import com.example.socket.domains.chat.exception.ChatErrorCode;
import com.example.socket.domains.chat.exception.ChatErrorException;
import com.example.socket.domains.chat.service.ChatCacheService;
import com.example.socket.domains.chat.domain.ChatId;
import com.example.socket.infra.client.internal.broker.MessageBrokerAdapter;
import com.example.socket.infra.common.exception.MessageBrokerErrorCode;
import com.example.socket.infra.common.exception.MessageBrokerErrorException;
import jakarta.persistence.OptimisticLockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class ChatMessageService {
    private final MessageBrokerAdapter messageBrokerAdapter;
    private final IdGenerator<Long> idGenerator;
    private final ChatCacheService chatCacheService;
    private final String CHAT_EXCHANGE_NAME;

    public ChatMessageService(
            IdGenerator<Long> tsidGenerator,
            MessageBrokerAdapter messageBrokerAdapter,
            ChatCacheService chatCacheService,
            @Value("${rabbitmq.chat-exchange.name}") String CHAT_EXCHANGE_NAME
    ) {
        this.idGenerator = tsidGenerator;
        this.messageBrokerAdapter = messageBrokerAdapter;
        this.chatCacheService = chatCacheService;
        this.CHAT_EXCHANGE_NAME = CHAT_EXCHANGE_NAME;
    }

    public void sendMessage(ChatMessage message, UserPrincipal principal) {
        ChatCache chat = createChatCache(message, principal); // 채팅 캐시 생성

        try {
            saveToRedis(chat); // 키-쌍 저장소에 저장
            publishToMessageBroker(message); // 메시지 브로커로 전송
        } catch (IllegalArgumentException | OptimisticLockException e) {
            log.error("메시지 저장 실패: {}", e.getMessage());
            throw new ChatErrorException(ChatErrorCode.SAVE_ERROR); // 500 -> redis. 도메인 에러
        } catch (AmqpException e) {
            log.error("메시지 전송 실패: {}", e.getMessage());
            throw new MessageBrokerErrorException(MessageBrokerErrorCode.SEND_ERROR); // 500 -> rabbitmq. 인프라 쪽 에러
        }
    }

    private ChatCache createChatCache(ChatMessage message, UserPrincipal principal) {
        return ChatCache.builder()
                .id(ChatId.of(idGenerator.execute(), Long.parseLong(message.roomId())))
                .messageFrom(principal.getUserId())
                .content(message.content())
                .createdAt(LocalDateTime.now())
                .build();
    }

    private void saveToRedis(ChatCache message) throws IllegalArgumentException, OptimisticLockException {
        chatCacheService.save(message);
    }

    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000))
    private void publishToMessageBroker(ChatMessage message) {
        messageBrokerAdapter.convertAndSend(CHAT_EXCHANGE_NAME, "chat.room." + message.roomId(), message);
    }
}
