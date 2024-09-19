package com.example.socket.chats.common.handler;

import com.example.socket.chats.common.handler.marker.ConnectCommandHandler;
import com.example.socket.chats.common.security.jwt.access.AccessTokenClaimKeys;
import com.example.socket.chats.common.security.jwt.access.AccessTokenProvider;
import com.example.socket.chats.common.security.principle.UserPrincipal;
import com.example.socket.domains.user.domain.User;
import com.example.socket.domains.user.service.UserService;
import com.example.socket.infra.common.exception.JwtErrorCode;
import com.example.socket.infra.common.exception.JwtErrorException;
import com.example.socket.infra.common.jwt.JwtClaims;
import com.example.socket.infra.common.jwt.JwtClaimsParserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticateHandler implements ConnectCommandHandler {
    private final AccessTokenProvider chatAccessTokenProvider;
    private final UserService userService;

    @Override
    public boolean isSupport(StompCommand command) {
        return StompCommand.CONNECT.equals(command);
    }

    @Override
    public void handle(Message<?> message, StompHeaderAccessor accessor) {
        String accessToken = extractAccessToken(accessor);

        JwtClaims claims = chatAccessTokenProvider.getJwtClaimsFromToken(accessToken);
        Long userId = JwtClaimsParserUtil.getClaimsValue(claims, AccessTokenClaimKeys.USER_ID.getValue(), Long::parseLong);

        authenticateUser(accessor, userId);
    }

    private String extractAccessToken(StompHeaderAccessor accessor) {
        String authorization = accessor.getFirstNativeHeader("Authorization");

        if ((authorization == null || !authorization.startsWith("Bearer "))) {
            log.warn("[인증 핸들러] 헤더에 Authorization이 없거나 Bearer 토큰이 아닙니다.");
            throw new JwtErrorException(JwtErrorCode.EMPTY_ACCESS_TOKEN);
        }

        return authorization.substring(7);
    }

    private void authenticateUser(StompHeaderAccessor accessor, Long userId) {
        User user = userService.readById(userId);
        Principal principal = UserPrincipal.from(user);

        log.info("[인증 핸들러] 사용자 인증 완료: {}", principal);

        accessor.setUser(principal);
    }
}
