package com.example.socket.chats.common.event;

import com.example.socket.chats.dto.ServerSideMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReceiptEventHandler {
    private final ObjectMapper objectMapper;

    @Bean
    @Async
    @EventListener
    public CompletableFuture<ApplicationListener<RefreshEvent<ServerSideMessage>>> handleRefreshEvent(final AbstractSubscribableChannel clientOutboundChannel) {
        return CompletableFuture.completedFuture(event -> {
            log.info("handleRefreshEvent: {}", event);
            Message<ServerSideMessage> message = event.getMessage();
            StompHeaderAccessor accessor = StompHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

            byte[] payload = new byte[0];
            try {
                log.info("message.getPayload(): {}", message.getPayload());
                payload = objectMapper.writeValueAsBytes(message.getPayload());
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            if (accessor != null && accessor.getReceipt() != null) {
                accessor.setHeader("stompCommand", StompCommand.RECEIPT);
                accessor.setReceiptId(accessor.getReceipt());

                Message<byte[]> receiptMessage = MessageBuilder.createMessage(payload, accessor.getMessageHeaders());
                log.info("receiptMessage: {}", receiptMessage);

                clientOutboundChannel.send(receiptMessage);
            }
        });
    }
}
