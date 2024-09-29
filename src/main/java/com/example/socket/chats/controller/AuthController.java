package com.example.socket.chats.controller;

import com.example.socket.chats.common.annotation.PreAuthorize;
import com.example.socket.chats.common.security.principal.UserPrincipal;
import com.example.socket.chats.dto.ChatMessage;
import com.example.socket.chats.service.AuthService;
import com.example.socket.infra.common.exception.JwtErrorCode;
import com.example.socket.infra.common.exception.JwtErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller("chatAuthController")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @MessageMapping("auth.refresh")
    @PreAuthorize("#principal instanceof T(com.example.socket.chats.common.security.principal.UserPrincipal)")
    public void refreshPrincipal(@Header("Authorization") String authorization, Principal principal, StompHeaderAccessor accessor) {
        log.info("refreshPrincipal AccessToken: {}", authorization);

        // token 앞의 "Bearer " 제거
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new JwtErrorException(JwtErrorCode.EMPTY_ACCESS_TOKEN);
        }
        String token = authorization.substring(7);

        authService.refreshPrincipal(token, (UserPrincipal) principal, accessor);
    }
}
