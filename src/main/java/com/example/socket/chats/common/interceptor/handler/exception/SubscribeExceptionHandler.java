package com.example.socket.chats.common.interceptor.handler.exception;

import com.example.socket.chats.common.event.SubscribeEvent;
import com.example.socket.chats.common.exception.InterceptorErrorException;
import com.example.socket.chats.dto.ServerSideMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscribeExceptionHandler implements StompExceptionHandler {
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public boolean canHandle(Throwable cause) {
        if (cause instanceof InterceptorErrorException ex) {
            return ex.getErrorCode().isSupportCommand(StompCommand.SUBSCRIBE);
        }
        return false;
    }

    @Override
    public Message<byte[]> handle(Message<byte[]> clientMessage, Throwable cause) {
        // header에 receipt 가 존재하는 지 확인
        if (clientMessage == null || !clientMessage.getHeaders().containsKey("receipt")) {
            log.warn("receipt header가 존재하지 않습니다. clientMessage={}", clientMessage);
            return null;
        }
        InterceptorErrorException ex = (InterceptorErrorException) cause;

        ServerSideMessage payload = ServerSideMessage.of(ex.causedBy().getCode(), ex.getErrorCode().getExplainError());
        Message<ServerSideMessage> message = MessageBuilder.createMessage(payload, clientMessage.getHeaders());

        eventPublisher.publishEvent(SubscribeEvent.of(message));

        return null; // client 연결 해지 안 함.
    }
}
