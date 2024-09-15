package com.example.socket.infra.client.chat;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultWebSocketProxyHandler implements WebSocketProxyHandler {
    private final ChatServerClient chatServerClient;

    @Override
    public ResponseEntity<byte[]> handle(HttpServletRequest request, HttpHeaders headers) {
        if (request.getMethod().equals(HttpMethod.GET.name())) {
            return chatServerClient.connectWebSocket(headers, getBody(request));
        } else if (request.getMethod().equals(HttpMethod.POST.name())) {
            return chatServerClient.connectWebSocketPost(headers, getBody(request));
        }

        throw new IllegalArgumentException("Unsupported method: " + request.getMethod());
    }

    private byte[] getBody(HttpServletRequest request) {
        try {
            return request.getInputStream().readAllBytes();
        } catch (Exception e) {
            log.error("Failed to read request body", e);
            return new byte[0];
        }
    }
}
