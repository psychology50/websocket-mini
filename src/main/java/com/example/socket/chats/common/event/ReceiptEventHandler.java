package com.example.socket.chats.common.event;

import com.example.socket.chats.dto.ServerSideMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.AbstractSubscribableChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReceiptEventHandler {
    private final ObjectMapper objectMapper;

    @Bean
    @Async
    @EventListener
    public CompletableFuture<ApplicationListener<RefreshEvent<ServerSideMessage>>> refreshEventListener(final AbstractSubscribableChannel clientOutboundChannel) {
        return CompletableFuture.completedFuture(event -> {
            log.info("handleRefreshEvent: {}", event);
            Message<ServerSideMessage> message = event.getMessage();
            StompHeaderAccessor accessor = StompHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

            byte[] payload = parsePayload(message);

            sendReceiptMessage(clientOutboundChannel, accessor, payload);
        });
    }

    @Bean
    @Async
    @EventListener
    public CompletableFuture<ApplicationListener<SubscribeEvent<ServerSideMessage>>> subscribeEventListener(final AbstractSubscribableChannel clientOutboundChannel) {
        return CompletableFuture.completedFuture(event -> {
            log.info("handleSessionSubscribeEvent: {}", event);
            Message<ServerSideMessage> message = event.getMessage();
            StompHeaderAccessor accessor = StompHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

            byte[] payload = parsePayload(message);

            sendReceiptMessage(clientOutboundChannel, accessor, payload);
        });
    }

    @Bean
    @Async
    @EventListener
    public CompletableFuture<ApplicationListener<SessionSubscribeEvent>> sessionSubscribeEventListener(final AbstractSubscribableChannel clientOutboundChannel) {
        return CompletableFuture.completedFuture(event -> {
            log.info("handleSessionSubscribeEvent: {}", event);
            Message<byte[]> message = event.getMessage();
            StompHeaderAccessor accessor = StompHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

            sendReceiptMessage(clientOutboundChannel, accessor, new byte[0]);
        });
    }

    /**
     * 메시지의 payload를 byte[]로 변환
     * @return 파라미터의 message를 byte[]로 변환한 값. 변환에 실패할 경우 빈 byte[] 반환
     */
    private byte[] parsePayload(Message<ServerSideMessage> message) {
        byte[] payload = new byte[0];
        try {
            log.info("message.getPayload(): {}", message.getPayload());
            payload = objectMapper.writeValueAsBytes(message.getPayload());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return payload;
    }

    private void sendReceiptMessage(AbstractSubscribableChannel clientOutboundChannel, StompHeaderAccessor accessor, byte[] payload) {
        if (accessor != null && accessor.getReceipt() != null) {
            accessor.setHeader("stompCommand", StompCommand.RECEIPT);
            accessor.setReceiptId(accessor.getReceipt());

            Message<byte[]> receiptMessage = MessageBuilder.createMessage(payload, accessor.getMessageHeaders());
            log.info("receiptMessage: {}", receiptMessage);

            clientOutboundChannel.send(receiptMessage);
        }
    }
}
