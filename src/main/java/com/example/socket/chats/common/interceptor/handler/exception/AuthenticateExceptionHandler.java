package com.example.socket.chats.common.interceptor.handler.exception;

import com.example.socket.chats.dto.ServerSideMessage;
import com.example.socket.infra.common.exception.JwtErrorException;
import com.example.socket.infra.common.jwt.JwtErrorCodeUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticateExceptionHandler implements StompExceptionHandler {
    private final ObjectMapper objectMapper;
    private static final byte[] EMPTY_PAYLOAD = new byte[0];

    @Override
    public boolean canHandle(Throwable cause) {
        return cause instanceof JwtErrorException;
    }

    @Override
    public Message<byte[]> handle(Message<byte[]> clientMessage, Throwable cause) {
        StompHeaderAccessor errorHeaderAccessor = StompHeaderAccessor.create(StompCommand.ERROR);

        ServerSideMessage serverSideMessage = null;
        if (cause instanceof JwtErrorException ex) {
            JwtErrorException jwtErrorException = JwtErrorCodeUtil.determineAuthErrorException(ex);
            log.error("[인증 예외] {}", jwtErrorException.getErrorCode().getMessage());

            errorHeaderAccessor.setMessage(jwtErrorException.getErrorCode().causedBy().getCode());
            errorHeaderAccessor.setLeaveMutable(true);
            serverSideMessage = ServerSideMessage.of(jwtErrorException.getErrorCode().getExplainError());
        }

        extractClientHeaderAccessor(clientMessage, errorHeaderAccessor);
        errorHeaderAccessor.setImmutable();

        return createMessage(errorHeaderAccessor, serverSideMessage);
    }

    private Message<byte[]> createMessage(StompHeaderAccessor errorHeaderAccessor, ServerSideMessage errorPayload) {
        if (errorPayload == null) {
            return MessageBuilder.createMessage(EMPTY_PAYLOAD, errorHeaderAccessor.getMessageHeaders());
        }

        try {
            byte[] payload = objectMapper.writeValueAsBytes(errorPayload);
            return MessageBuilder.createMessage(payload, errorHeaderAccessor.getMessageHeaders());
        } catch (Exception e) {
            log.error("[인증 예외] 에러 메시지 생성 중 오류가 발생했습니다.", e);
            return MessageBuilder.createMessage(EMPTY_PAYLOAD, errorHeaderAccessor.getMessageHeaders());
        }
    }
}
