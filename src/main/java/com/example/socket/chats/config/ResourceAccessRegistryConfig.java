package com.example.socket.chats.config;

import com.example.socket.chats.common.security.authorize.ChatRoomAccessChecker;
import com.example.socket.chats.common.security.authorize.ResourceAccessRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ResourceAccessRegistryConfig {
    private final ChatRoomAccessChecker chatRoomChecker;

    @Bean
    public ResourceAccessRegistry configureResourceAccess() {
        ResourceAccessRegistry registry = new ResourceAccessRegistry();

        registry.registerChecker("^/sub/chat\\.exchange/chat\\.room\\.\\d+$", chatRoomChecker);

        return registry;
    }
}
