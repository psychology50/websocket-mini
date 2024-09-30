package com.example.socket.chats.common.interceptor.handler.exception;

import com.example.socket.chats.common.event.SubscribeEvent;
import com.example.socket.chats.common.exception.InterceptorErrorException;
import com.example.socket.chats.common.interceptor.handler.AbstractStompExceptionHandler;
import com.example.socket.chats.dto.ServerSideMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Slf4j
@Component
public class SubscribeExceptionHandler extends AbstractStompExceptionHandler {
    public SubscribeExceptionHandler(ObjectMapper objectMapper) {
        super(objectMapper);
    }

    @Override
    public boolean canHandle(Throwable cause) {
        if (cause instanceof InterceptorErrorException ex) {
            return ex.getErrorCode().isSupportCommand(StompCommand.SUBSCRIBE);
        }
        return false;
    }

    @Override
    protected StompCommand getStompCommand() {
        return StompCommand.RECEIPT;
    }

    @Override
    protected ServerSideMessage getServerSideMessage(Throwable cause) {
        InterceptorErrorException ex = (InterceptorErrorException) cause;
        return ServerSideMessage.of(ex.causedBy().getCode(), ex.getErrorCode().getExplainError());
    }

    @Override
    protected boolean isNullReturnRequired(Message<byte[]> clientMessage) {
        if (clientMessage == null) {
            log.warn("receipt header가 존재하지 않습니다. clientMessage={}", clientMessage);
            return true;
        }

        StompHeaderAccessor accessor = StompHeaderAccessor.getAccessor(clientMessage, StompHeaderAccessor.class);

        if (accessor == null || accessor.getReceipt() == null) {
            log.warn("receipt header가 존재하지 않습니다. accessor={}", accessor);
            return true;
        }

        return false;
    }
}
