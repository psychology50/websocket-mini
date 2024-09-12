package com.example.socket.chats.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompReactorNettyCodec;
import org.springframework.messaging.tcp.reactor.ReactorNettyCodec;
import org.springframework.messaging.tcp.reactor.ReactorNettyTcpClient;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import reactor.netty.tcp.SslProvider;
import reactor.netty.tcp.TcpClient;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
//                .setAllowedOrigins("*") // 이거 넣으면 allowedOrigins가 true일 때, * 못 넣으니까 pattern 쓰라고 에러 발생함.
                .setAllowedOriginPatterns("*") // 실제 환경에선 API 서버 도메인만 허용
                .withSockJS(); // JS 라이브러리. 우린 iOS라서 안 씀. 테스트를 위해 허용
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        TcpClient tcpClient = TcpClient
                .create()
                .host("localhost")
                .port(61613);
//                .secure(SslProvider.defaultClientProvider());

        ReactorNettyTcpClient<byte[]> client = new ReactorNettyTcpClient<>(tcpClient, new StompReactorNettyCodec());

        config.enableStompBrokerRelay("/queue", "/topic", "/exchange", "/amq/queue")
                .setAutoStartup(true)
                .setTcpClient(client) // RabbitMQ와 연결할 클라이언트 설정
                .setRelayHost("localhost") // RabbitMQ 서버 주소
                .setRelayPort(61613) // RabbitMQ 포트(5672), STOMP(61613)
                .setSystemLogin("jayang") // RabbitMQ 시스템 계정
                .setSystemPasscode("secret") // RabbitMQ 시스템 비밀번호
                .setClientLogin("jayang") // RabbitMQ 클라이언트 계정
                .setClientPasscode("secret"); // RabbitMQ 클라이언트 비밀번호

        config.setPathMatcher(new AntPathMatcher(".")); // url을 chat/room/3 -> chat.room.3으로 참조하기 위한 설정
        config.setApplicationDestinationPrefixes("/pub"); // 클라이언트에서 메시지 송신 시 프리픽스
    }
}
