package com.example.socket.chats.service;

import com.example.socket.chats.common.security.principal.UserPrincipal;
import com.example.socket.chats.dto.ServerSideMessage;
import com.example.socket.infra.common.exception.JwtErrorException;
import com.example.socket.infra.common.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final JwtProvider chatAccessTokenProvider;
    private final RabbitTemplate rabbitTemplate;

    public void refreshPrincipal(String token, UserPrincipal principal) { // 실패했을 때, 연결 끊어버릴 건지??
        try {
            LocalDateTime expiresAt = chatAccessTokenProvider.getExpiryDate(token);
            principal.updateExpiresAt(expiresAt);

            log.info("refresh success: {}", principal);
            rabbitTemplate.convertAndSend("auth", ServerSideMessage.of("2000", "토큰 갱신 성공"));
        } catch (JwtErrorException e) {
            log.info("refresh failed: {}", e.getErrorCode().getExplainError());
            rabbitTemplate.convertAndSend("auth", ServerSideMessage.of(e.causedBy().getCode(), e.getErrorCode().getExplainError()));
        }
    }
}
