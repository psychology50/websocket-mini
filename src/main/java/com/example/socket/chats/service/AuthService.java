package com.example.socket.chats.service;

import com.example.socket.chats.common.event.RefreshEvent;
import com.example.socket.chats.common.security.jwt.access.AccessTokenProvider;
import com.example.socket.chats.common.security.principal.UserPrincipal;
import com.example.socket.chats.dto.ServerSideMessage;
import com.example.socket.infra.common.exception.JwtErrorException;
import com.example.socket.infra.common.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final AccessTokenProvider chatAccessTokenProvider;
    private final ApplicationEventPublisher eventPublisher;

    public void refreshPrincipal(String token, UserPrincipal principal, StompHeaderAccessor accessor) { // 실패했을 때, 연결 끊어버릴 건지??
        // Receipt 프레임으로 응답 보낼 거라 RabbitMQ로 보낼 필요가 없음. event로 처리.
        try {
            LocalDateTime expiresAt = chatAccessTokenProvider.getExpiryDate(token);
            principal.updateExpiresAt(expiresAt);

            log.info("refresh success: {}", principal);

            Message<ServerSideMessage> message = MessageBuilder.createMessage(ServerSideMessage.of("2000", "토큰 갱신 성공"), accessor.getMessageHeaders());

            eventPublisher.publishEvent(RefreshEvent.of(message));
        } catch (JwtErrorException e) {
            log.info("refresh failed: {}", e.getErrorCode().getExplainError());

            Message<ServerSideMessage> message = MessageBuilder.createMessage(ServerSideMessage.of(e.getErrorCode().getExplainError()), accessor.getMessageHeaders());

            eventPublisher.publishEvent(RefreshEvent.of(message));
        }
    }
}
