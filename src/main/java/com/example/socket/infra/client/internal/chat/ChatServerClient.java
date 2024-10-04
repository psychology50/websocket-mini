package com.example.socket.infra.client.internal.chat;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "chatServer", url = "http://localhost:8080/chat")
public interface ChatServerClient {
    @RequestMapping(value = "", method = {RequestMethod.GET})
    ResponseEntity<byte[]> getWebSocketInfo(
            @RequestHeader HttpHeaders headers,
            @RequestBody(required = false) byte[] body
    );

}
