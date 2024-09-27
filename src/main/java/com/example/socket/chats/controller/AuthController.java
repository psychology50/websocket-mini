package com.example.socket.chats.controller;

import com.example.socket.chats.common.annotation.PreAuthorize;
import com.example.socket.chats.common.security.principal.UserPrincipal;
import com.example.socket.chats.dto.ChatMessage;
import com.example.socket.chats.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @MessageMapping("auth.refresh")
    @PreAuthorize("principal instanceof T(UserPrincipal)")
    public void refreshPrincipal(@Header("Authorization") String token, Principal principal) {
        log.info("refreshPrincipal AccessToken: {}", token);

        authService.refreshPrincipal(token, (UserPrincipal) principal);
    }
}
