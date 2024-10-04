package com.example.socket.infra.client.internal.chat.dto;

import java.util.List;
import java.util.Map;

public class WebSocket {
    public record Url(String url) {}

    public record Info<K, V> (Map<K, List<V>> headers, byte[] body) {
    }
}
