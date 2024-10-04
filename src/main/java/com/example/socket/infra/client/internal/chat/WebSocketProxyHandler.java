package com.example.socket.infra.client.internal.chat;

import com.example.socket.infra.client.internal.chat.dto.WebSocket;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;

public interface WebSocketProxyHandler {
    WebSocket.Url getWebSocketServerUrl(HttpServletRequest request, HttpHeaders headers);

//    ResponseEntity<byte[]> handle(HttpServletRequest request, HttpHeaders headers);
}
