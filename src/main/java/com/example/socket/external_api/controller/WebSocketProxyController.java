package com.example.socket.external_api.controller;

import com.example.socket.infra.client.internal.chat.WebSocketProxyHandler;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/ws")
@CrossOrigin(originPatterns = "*", methods = {RequestMethod.GET}, allowedHeaders = "*", allowCredentials = "true")
public class WebSocketProxyController {

    private final WebSocketProxyHandler webSocketProxyHandler;

    @GetMapping("")
    public ResponseEntity<?> getWebSocketServerUrl(
            HttpServletRequest request,
            @RequestHeader HttpHeaders headers
    ) {
        return ResponseEntity.ok(webSocketProxyHandler.getWebSocketServerUrl(request, headers));
    }
//
//    @GetMapping("/info")
//    public ResponseEntity<StreamingResponseBody> getWebSocketServerInfo(
//            HttpServletRequest request,
//            @RequestHeader HttpHeaders headers
////            @RequestParam("t") String timestamp
//    ) {
//        log.info("getWebSocketServerInfo: {}", headers);
//
//        ResponseEntity<byte[]> response = webSocketProxyHandler.handle(request, headers);
//        log.info("response: {}", response);
//
//        HttpHeaders responseHeaders = new HttpHeaders();
//        responseHeaders.putAll(response.getHeaders());
//        log.info("responseHeaders: {}", responseHeaders);
//
//        return ResponseEntity.status(response.getStatusCode())
//                .headers(responseHeaders)
//                .body(outputStream -> {
//                    if (response.getBody() != null) {
//                        outputStream.write(response.getBody());
//                    }
//                });
//    }
}
