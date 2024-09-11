package com.example.socket.chats.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*") // 실제 환경에선 API 서버 도메인만 허용
                .withSockJS(); // JS 라이브러리. 우린 iOS라서 안 씀. 테스트를 위해 허용
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableStompBrokerRelay("/sub")
                .setRelayHost("localhost") // RabbitMQ 서버 주소
                .setRelayPort(5672) // RabbitMQ 포트
                .setClientLogin("jayang") // RabbitMQ 클라이언트 계정
                .setClientPasscode("secret"); // RabbitMQ 클라이언트 비밀번호

        config.setPathMatcher(new AntPathMatcher(".")); // url을 chat/room/3 -> chat.room.3으로 참조하기 위한 설정
        config.setApplicationDestinationPrefixes("/pub"); // 클라이언트에서 메시지 송신 시 프리픽스
    }
}
