package com.example.socket.chats.common.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StompInboundInterceptor implements ChannelInterceptor {
    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        log.info("Inbound message: {}", message);

        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        log.info("preSend: accessor={}", accessor);

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String destination = accessor.getDestination();
            String sessionId = accessor.getSessionId();

            log.info("preSend: destination={}, sessionId={}", destination, sessionId);

            // 구독 전 필요한 처리 수행 (ex. 사용자 권한 확인, 구독 대상 변경, 로깅 등)

            if (destination != null && destination.startsWith("/topic/")) {
                // RabbitMQ exchange 직접 접근 방지
                String convertedDestination = convertDestination(destination);
                log.info("preSend: convertedDestination={}", convertedDestination);
                accessor.setDestination(convertedDestination);
            }
        }

        return message;
    }

    private String convertDestination(String originalDestination) {
        return originalDestination.replace("/topic/", "/exchange/chat.exchange/");
    }
}
