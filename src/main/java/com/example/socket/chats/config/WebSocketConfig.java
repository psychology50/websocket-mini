package com.example.socket.chats.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        log.info("registerStompEndpoints: /ws");

        registry.addEndpoint("/ws")
                .setAllowedOrigins("*"); // 실제 환경에선 API 서버 도메인만 허용
//                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableStompBrokerRelay("/sub") // RabbitMQ를 사용하는 경우
                .setRelayHost("localhost") // RabbitMQ 서버 주소
                .setRelayPort(5672) // RabbitMQ 포트
                .setClientLogin("jayang") // RabbitMQ 클라이언트 계정
                .setClientPasscode("secret"); // RabbitMQ 클라이언트 비밀번호

        config.setApplicationDestinationPrefixes("/pub"); // 클라이언트에서 메시지 송신 시 프리픽스
    }
}
