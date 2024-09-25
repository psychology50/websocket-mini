package com.example.socket.chats.common.handler.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompExceptionHandler extends StompSubProtocolErrorHandler {
    private final List<StompExceptionInterceptor> interceptors;

    @Override
    @Nullable
    public Message<byte[]> handleClientMessageProcessingError(@Nullable Message<byte[]> clientMessage, Throwable ex) {
        for (StompExceptionInterceptor interceptor : interceptors) {
            if (interceptor.canHandle(ex)) {
                return interceptor.handle(clientMessage, ex);
            }
        }

        log.error("STOMP client message processing error", ex);
        return super.handleClientMessageProcessingError(clientMessage, ex);
    }
}
