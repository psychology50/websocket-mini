package com.example.socket.chats.controller;

import com.example.socket.chats.common.annotation.PreAuthorize;
import com.example.socket.chats.common.security.principal.UserPrincipal;
import com.example.socket.chats.dto.ChatMessage;
import com.example.socket.chats.service.AuthService;
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
    public void refreshPrincipal(@Header("Authorization") String token, Principal principal, StompHeaderAccessor accessor) {
        log.info("refreshPrincipal AccessToken: {}", token);

        // token 앞의 "Bearer " 제거
        token = token.substring(7);

        authService.refreshPrincipal(token, (UserPrincipal) principal, accessor);
    }
}