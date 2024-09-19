package com.example.socket.chats.common.handler;

import com.example.socket.chats.common.handler.marker.SubscribeCommandHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExchangeConvertHandler implements SubscribeCommandHandler {
    private static final String REQUEST_EXCHANGE_PREFIX = "/sub/";
    private static final String CONVERTED_EXCHANGE_PREFIX = "/exchange/chat.exchange/";

    @Override
    public boolean supports(StompCommand command) {
        return StompCommand.SUBSCRIBE.equals(command);
    }

    @Override
    public void handle(Message<?> message, StompHeaderAccessor accessor) {
        String destination = accessor.getDestination();

        if (destination != null && destination.startsWith(REQUEST_EXCHANGE_PREFIX)) {
            String convertedDestination = convertDestination(destination);
            log.info("[Exchange 변환 핸들러] destination={}, convertedDestination={}", destination, convertedDestination);

            accessor.setDestination(convertedDestination);
        }
    }

    private String convertDestination(String destination) {
        return destination.replace(REQUEST_EXCHANGE_PREFIX, CONVERTED_EXCHANGE_PREFIX);
    }
}
