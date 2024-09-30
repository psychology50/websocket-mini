package com.example.socket.chats.common.interceptor.handler.exception;

import com.example.socket.chats.common.interceptor.handler.AbstractStompExceptionHandler;
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
public class AuthenticateExceptionHandler extends AbstractStompExceptionHandler {
    public AuthenticateExceptionHandler(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public boolean canHandle(Throwable cause) {
        return cause instanceof JwtErrorException;
    }

    @Override
    protected StompCommand getStompCommand() {
        return StompCommand.ERROR;
    }

    @Override
    protected ServerSideMessage getServerSideMessage(Throwable cause) {
        JwtErrorException ex = (JwtErrorException) cause;
        ex = JwtErrorCodeUtil.determineAuthErrorException(ex);

        log.warn("[인증 예외] {}", ex.getErrorCode().getMessage());

        return ServerSideMessage.of(ex.getErrorCode().getExplainError());
    }
}
