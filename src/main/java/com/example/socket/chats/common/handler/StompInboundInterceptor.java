package com.example.socket.chats.common.handler;

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
    private final AccessTokenProvider chatAccessTokenProvider;
    private final UserService userService;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        log.info("Inbound message: {}", message);

        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        log.info("preSend: accessor={}", accessor);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
//            Object authorization = accessor.getHeader("Authorization"); // 이거 왜 안 됨 ㅋㅋ
            String authorization = accessor.getFirstNativeHeader("Authorization");
            log.info("preSend: authorization={}", authorization);

            if (authorization != null && authorization.startsWith("Bearer ")) {
                String accessToken = authorization.substring(7);
                log.info("preSend: accessToken={}", accessToken);

                JwtClaims claims = chatAccessTokenProvider.getJwtClaimsFromToken(accessToken);
                log.info("presSend: accessToken claims={}", claims);

                Long userId = JwtClaimsParserUtil.getClaimsValue(claims, AccessTokenClaimKeys.USER_ID.getValue(), Long::parseLong);
                log.info("preSend: userId={}", userId);

                User user = userService.readById(userId);
                Principal principal = UserPrincipal.from(user);

                accessor.setUser(principal); // 채팅 유저 설정
            }
        }

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String destination = accessor.getDestination();
            String sessionId = accessor.getSessionId();

            log.info("preSend: destination={}, sessionId={}", destination, sessionId);

            if (destination != null && destination.startsWith("/sub/")) { // client /sub/ 요청을 /exchange/chat.exchange/로 변경
                // RabbitMQ exchange 직접 접근 방지
                String convertedDestination = convertDestination(destination);
                log.info("preSend: convertedDestination={}", convertedDestination);
                accessor.setDestination(convertedDestination);
            }
        }

        return message;
    }

    private String convertDestination(String originalDestination) {
        return originalDestination.replace("/sub/", "/exchange/chat.exchange/");
    }
}
