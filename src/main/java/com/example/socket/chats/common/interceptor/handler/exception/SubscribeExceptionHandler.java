package com.example.socket.chats.common.interceptor.handler.exception;

import com.example.socket.chats.common.event.SubscribeEvent;
import com.example.socket.chats.common.exception.InterceptorErrorException;
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
@RequiredArgsConstructor
public class SubscribeExceptionHandler implements StompExceptionHandler {
    private final ApplicationEventPublisher eventPublisher;
    private final ObjectMapper objectMapper;
    private static final byte[] EMPTY_PAYLOAD = new byte[0];

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
        if (clientMessage == null) {
            log.warn("receipt header가 존재하지 않습니다. clientMessage={}", clientMessage);
            return null;
        }

        StompHeaderAccessor accessor = StompHeaderAccessor.getAccessor(clientMessage, StompHeaderAccessor.class);

        if (accessor == null || accessor.getReceipt() == null) {
            log.warn("receipt header가 존재하지 않습니다. accessor={}", accessor);
            return null;
        }

        log.info("receipt header가 존재합니다. receipt={}", accessor.getReceipt());

        InterceptorErrorException ex = (InterceptorErrorException) cause;

        ServerSideMessage payload = ServerSideMessage.of(ex.causedBy().getCode(), ex.getErrorCode().getExplainError());
//        Message<ServerSideMessage> message = MessageBuilder.createMessage(payload, accessor.getMessageHeaders());

//        eventPublisher.publishEvent(SubscribeEvent.of(message)); // 이렇게 처리 안 됨.

        StompHeaderAccessor errorHeaderAccessor = StompHeaderAccessor.create(StompCommand.RECEIPT); // 직접 RECIPT 메시지를 생성해서 반환
        errorHeaderAccessor.setReceiptId(accessor.getReceipt());
        errorHeaderAccessor.setLeaveMutable(true);
        extractClientHeaderAccessor(clientMessage, errorHeaderAccessor);
        errorHeaderAccessor.setImmutable();

        return createMessage(errorHeaderAccessor, payload); // client 연결 해지 안 함.
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
