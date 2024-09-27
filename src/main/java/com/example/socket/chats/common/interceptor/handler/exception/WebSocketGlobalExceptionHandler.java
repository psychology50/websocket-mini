package com.example.socket.chats.common.interceptor.handler.exception;

import com.example.socket.chats.dto.ErrorMessage;
import com.example.socket.config.exception.GlobalErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.converter.SimpleMessageConverter;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.security.Principal;

@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class WebSocketGlobalExceptionHandler {
    private final SimpMessagingTemplate template;
    private static final String ERROR_DESTINATION = "/queue/errors";

    @MessageExceptionHandler(GlobalErrorException.class)
    public void handleGlobalErrorException(Principal principal, GlobalErrorException ex) {
        ErrorMessage errorMessage = ErrorMessage.of(ex.causedBy().getCode(), ex.getBaseErrorCode().getExplainError());
        log.error("handleGlobalErrorException: {}", errorMessage);

        template.convertAndSendToUser(principal.getName(), ERROR_DESTINATION, errorMessage);
    }

    @MessageExceptionHandler(RuntimeException.class)
    public void handleRuntimeException(Principal principal, RuntimeException ex) {
        ErrorMessage errorMessage = ErrorMessage.of("5000", ex.getMessage());
        log.error("handleRuntimeException: {}", errorMessage);

        template.convertAndSendToUser(principal.getName(), ERROR_DESTINATION, errorMessage);
    }
}
