package com.example.socket.infra.client.chat;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

public interface WebSocketProxyHandler {
    ResponseEntity<byte[]> handle(HttpServletRequest request, HttpHeaders headers);
}
