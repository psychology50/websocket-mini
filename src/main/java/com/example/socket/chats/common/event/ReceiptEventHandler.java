package com.example.socket.chats.common.event;

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

@Slf4j
@Component
public class ReceiptEventHandler {
    @Bean
    @Async
    @EventListener
    public ApplicationListener<RefreshEvent> handleRefreshEvent(final AbstractSubscribableChannel clientOutboundChannel) {
        return event -> {
            log.info("handleRefreshEvent: {}", event);
            Message<byte[]> message = event.getMessage();
            StompHeaderAccessor accessor = StompHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

            if (accessor.getReceipt() != null) {
                accessor.setHeader("stompCommand", StompCommand.RECEIPT);
                accessor.setReceiptId(accessor.getReceipt());
                clientOutboundChannel.send(
                        MessageBuilder.createMessage(new byte[0], accessor.getMessageHeaders()));
            }
        };
    }
}
