package com.example.socket.external_api.controller;

import com.example.socket.infra.client.chat.WebSocketProxyHandler;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", methods = {RequestMethod.GET}, allowedHeaders = "*", allowCredentials = "true")
public class WebSocketProxyController {

    private final WebSocketProxyHandler webSocketProxyHandler;

    @GetMapping("/ws/info")
    public ResponseEntity<StreamingResponseBody> connectWebSocketSub(
            HttpServletRequest request,
            @RequestHeader HttpHeaders headers
//            @RequestParam("t") String timestamp
    ) {
        log.info("connectWebSocketSub: {}", headers);

        ResponseEntity<byte[]> response = webSocketProxyHandler.handle(request, headers);
        log.info("response: {}", response);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.putAll(response.getHeaders());
        log.info("responseHeaders: {}", responseHeaders);

        return ResponseEntity.status(response.getStatusCode())
                .headers(responseHeaders)
                .body(outputStream -> {
                    if (response.getBody() != null) {
                        outputStream.write(response.getBody());
                    }
                });
    }
}
