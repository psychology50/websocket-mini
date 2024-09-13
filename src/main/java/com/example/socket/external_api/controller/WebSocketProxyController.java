package com.example.socket.external_api.controller;

import com.example.socket.infra.client.chat.WebSocketProxyHandler;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WebSocketProxyController {
    private final static String WEB_SOCKET = "websocket";
    private final static String UPGRADE = "Upgrade";

    private final WebSocketProxyHandler webSocketProxyHandler;

    @GetMapping("/ws")
    public ResponseEntity<StreamingResponseBody> connectWebSocket(
            HttpServletRequest request,
            @RequestHeader HttpHeaders headers
    ) {
        if (WEB_SOCKET.equals(headers.getFirst(UPGRADE))) {
            ResponseEntity<byte[]> response = webSocketProxyHandler.handle(request, headers);

            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.putAll(response.getHeaders());

            return ResponseEntity.status(response.getStatusCode())
                    .headers(responseHeaders)
                    .body(outputStream -> {
                        if (response.getBody() != null) {
                            outputStream.write(response.getBody());
                        }
                    });
        }

        return ResponseEntity.badRequest().body(outputStream -> {
            outputStream.write("WebSocket connection is required.".getBytes());
        });
    }
}
