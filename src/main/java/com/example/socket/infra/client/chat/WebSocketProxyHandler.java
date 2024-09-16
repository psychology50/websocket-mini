package com.example.socket.infra.client.chat;

import com.example.socket.infra.client.chat.dto.WebSocket;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

public interface WebSocketProxyHandler {
    WebSocket.Url getWebSocketServerUrl(HttpServletRequest request, HttpHeaders headers);

//    ResponseEntity<byte[]> handle(HttpServletRequest request, HttpHeaders headers);
}
