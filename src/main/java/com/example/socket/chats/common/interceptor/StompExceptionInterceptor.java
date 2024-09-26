package com.example.socket.chats.common.interceptor;

import com.example.socket.chats.common.handler.exception.StompExceptionHandler;
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
public class StompExceptionInterceptor extends StompSubProtocolErrorHandler {
    private final List<StompExceptionHandler> interceptors;

    @Override
    @Nullable
    public Message<byte[]> handleClientMessageProcessingError(@Nullable Message<byte[]> clientMessage, Throwable ex) {
        Throwable cause = ex.getCause();

        for (StompExceptionHandler interceptor : interceptors) {
            if (interceptor.canHandle(cause)) {
                log.error("STOMP client message processing error", cause);
                return interceptor.handle(clientMessage, cause);
            }
        }

        log.error("STOMP client message processing error", ex);
        return super.handleClientMessageProcessingError(clientMessage, ex);
    }
}
