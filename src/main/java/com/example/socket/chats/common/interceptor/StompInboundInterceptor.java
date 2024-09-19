package com.example.socket.chats.common.interceptor;

import com.example.socket.chats.common.handler.StompCommandHandler;
import com.example.socket.chats.common.handler.StompCommandHandlerFactory;
import com.example.socket.chats.common.security.jwt.access.AccessTokenClaimKeys;
import com.example.socket.chats.common.security.jwt.access.AccessTokenProvider;
import com.example.socket.chats.common.security.principle.UserPrincipal;
import com.example.socket.domains.user.domain.User;
import com.example.socket.domains.user.service.UserService;
import com.example.socket.infra.common.jwt.JwtClaims;
import com.example.socket.infra.common.jwt.JwtClaimsParserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompInboundInterceptor implements ChannelInterceptor {
    private final StompCommandHandlerFactory stompCommandHandlerFactory;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && accessor.getCommand() != null) {
            log.info("[StompInboundInterceptor] command={}", accessor.getCommand());

            for (StompCommandHandler handler: stompCommandHandlerFactory.getHandlers(accessor.getCommand())) {
                handler.handle(message, accessor);
            }
        }

        return message;
    }
}
