package com.example.socket.chats.config;

import com.example.socket.chats.common.handler.StompInboundInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompReactorNettyCodec;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.tcp.reactor.ReactorNettyTcpClient;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import reactor.netty.tcp.TcpClient;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class WebBrokerSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final StompInboundInterceptor stompInboundInterceptor;
    private final String RABBITMQ_HOST;

    public WebBrokerSocketConfig(
            StompInboundInterceptor stompInboundInterceptor,
            @Value("${spring.rabbitmq.host}") String rabbitmqHost
    ) {
        this.stompInboundInterceptor = stompInboundInterceptor;
        this.RABBITMQ_HOST = rabbitmqHost;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat")
//                .setAllowedOrigins("*") // 이거 넣으면 allowedOrigins가 true일 때, * 못 넣으니까 pattern 쓰라고 에러 발생함.
                .setAllowedOriginPatterns("*") // 실제 환경에선 API 서버 도메인만 허용
                .withSockJS(); // JS 라이브러리. 우린 iOS라서 안 씀. 테스트를 위해 허용
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        TcpClient tcpClient = TcpClient
                .create()
                .host(RABBITMQ_HOST)
                .port(61613);
//                .secure(SslProvider.defaultClientProvider());

        ReactorNettyTcpClient<byte[]> client = new ReactorNettyTcpClient<>(tcpClient, new StompReactorNettyCodec());

        config.enableStompBrokerRelay("/queue", "/topic", "/exchange", "/amq/queue")
                .setAutoStartup(true)
                .setTcpClient(client) // RabbitMQ와 연결할 클라이언트 설정
                .setRelayHost(RABBITMQ_HOST) // RabbitMQ 서버 주소
                .setRelayPort(61613) // RabbitMQ 포트(5672), STOMP(61613)
                .setSystemLogin("jayang") // RabbitMQ 시스템 계정
                .setSystemPasscode("secret") // RabbitMQ 시스템 비밀번호
                .setClientLogin("jayang") // RabbitMQ 클라이언트 계정
                .setClientPasscode("secret"); // RabbitMQ 클라이언트 비밀번호

        config.setPathMatcher(new AntPathMatcher(".")); // url을 chat/room/3 -> chat.room.3으로 참조하기 위한 설정
        config.setApplicationDestinationPrefixes("/pub"); // 클라이언트에서 메시지 송신 시 프리픽스
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(new ChannelInterceptor() {
//            @Override
//            public Message<?> preSend(Message<?> message, MessageChannel channel) {
//                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//                System.out.println("Inbound message: " + message);
//                return message;
//            }
//        });
        registration.interceptors(stompInboundInterceptor);
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
                System.out.println("Outbound message: " + message);
                return message;
            }
        });
    }
}
