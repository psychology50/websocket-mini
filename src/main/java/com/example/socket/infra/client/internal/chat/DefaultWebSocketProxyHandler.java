package com.example.socket.infra.client.internal.chat;

import com.example.socket.infra.client.internal.chat.dto.WebSocket;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultWebSocketProxyHandler implements WebSocketProxyHandler {
    private final ChatServerClient chatServerClient;

    @Override
    public WebSocket.Url getWebSocketServerUrl(HttpServletRequest request, HttpHeaders headers) {
        return new WebSocket.Url("ws://localhost:8000/chat");
    }
//
//    @Override
//    public ResponseEntity<byte[]> handle(HttpServletRequest request, HttpHeaders headers) {
//        return chatServerClient.getWebSocketInfo(headers, getBody(request));
//    }

    private byte[] getBody(HttpServletRequest request) {
        try {
            return request.getInputStream().readAllBytes();
        } catch (Exception e) {
            log.error("Failed to read request body", e);
            return new byte[0];
        }
    }
}
