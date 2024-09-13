package com.example.socket.infra.client.chat;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "chatServer", url = "${chat.server.url}")
public interface ChatServerClient {
    @RequestMapping(value = "/ws", method = {RequestMethod.GET})
    ResponseEntity<byte[]> connectWebSocket(
            @RequestHeader HttpHeaders headers,
            @RequestBody(required = false) byte[] body
    );
}
